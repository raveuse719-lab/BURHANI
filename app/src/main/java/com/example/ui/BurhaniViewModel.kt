package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BurhaniRepository
import com.example.data.entity.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class AppNavTab {
    DASHBOARD, CUSTOMERS, PRODUCTS, REPAIRS, INVOICES, WARRANTY, REPORTS, SUPPLIERS, SETTINGS
}

data class InvoiceLineItem(
    val productId: Long? = null,
    val name: String,
    val qty: Int = 1,
    val unitPrice: Double = 0.0,
    val lineTotal: Double = qty * unitPrice
)

class BurhaniViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BurhaniRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = BurhaniRepository(db)
    }

    // Active Tab
    private val _currentTab = MutableStateFlow(AppNavTab.DASHBOARD)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    // Current Active User / Staff
    private val _currentUser = MutableStateFlow(User(id = 1, username = "Abdeali (Admin)", role = "ADMIN", pin = "1234"))
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    fun loginWithPin(pin: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByPin(pin)
            if (user != null) {
                _currentUser.value = user
                onResult(true, "Welcome back, ${user.username}!")
            } else {
                onResult(false, "Invalid PIN. Default Admin PIN is 1234, Staff PIN is 0000.")
            }
        }
    }

    // Business Profile
    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateProfile(profile: BusinessProfile) {
        viewModelScope.launch {
            repository.updateBusinessProfile(profile)
        }
    }

    // Customers
    private val _customerSearch = MutableStateFlow("")
    val customerSearch: StateFlow<String> = _customerSearch.asStateFlow()

    fun setCustomerSearch(query: String) {
        _customerSearch.value = query
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val customersList: StateFlow<List<Customer>> = _customerSearch
        .flatMapLatest { query ->
            if (query.isBlank()) repository.customers else repository.searchCustomers(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveCustomer(customer: Customer) {
        viewModelScope.launch {
            if (customer.id == 0L) {
                repository.insertCustomer(customer)
            } else {
                repository.updateCustomer(customer)
            }
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    // Products & Inventory
    private val _productSearch = MutableStateFlow("")
    val productSearch: StateFlow<String> = _productSearch.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("ALL")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    fun setProductSearch(query: String) {
        _productSearch.value = query
    }

    fun setCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val productsList: StateFlow<List<Product>> = combine(_productSearch, _selectedCategoryFilter) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        val flow = if (query.isBlank()) repository.products else repository.searchProducts(query)
        flow.map { list ->
            if (category == "ALL") list else list.filter { it.category.equals(category, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockList: StateFlow<List<Product>> = repository.lowStockProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            if (product.id == 0L) {
                val newId = repository.insertProduct(product)
                repository.recordStockMovement(
                    StockMovement(
                        productId = newId,
                        productName = product.name,
                        type = "PURCHASE_IN",
                        quantityChange = product.stockQuantity,
                        notes = "Initial stock entry"
                    )
                )
            } else {
                repository.updateProduct(product)
            }
        }
    }

    fun adjustStock(productId: Long, productName: String, qtyChange: Int, reason: String) {
        viewModelScope.launch {
            repository.updateStock(productId, qtyChange)
            repository.recordStockMovement(
                StockMovement(
                    productId = productId,
                    productName = productName,
                    type = if (qtyChange > 0) "PURCHASE_IN" else "SALE_OUT",
                    quantityChange = qtyChange,
                    notes = reason
                )
            )
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // Repair Jobs
    private val _repairStatusFilter = MutableStateFlow("ALL")
    val repairStatusFilter: StateFlow<String> = _repairStatusFilter.asStateFlow()

    fun setRepairStatusFilter(status: String) {
        _repairStatusFilter.value = status
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val repairJobsList: StateFlow<List<RepairJob>> = _repairStatusFilter
        .flatMapLatest { status ->
            if (status == "ALL") repository.repairJobs else repository.getRepairJobsByStatus(status)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveRepairJob(job: RepairJob) {
        viewModelScope.launch {
            if (job.id == 0L) {
                // generate job number if needed
                val nextJobNo = "REP-" + (1000 + (repairJobsList.value.size + 1))
                val jobToInsert = job.copy(jobNo = if (job.jobNo.isBlank()) nextJobNo else job.jobNo)
                repository.insertRepairJob(jobToInsert)
            } else {
                repository.updateRepairJob(job)
            }
        }
    }

    fun updateRepairStatus(job: RepairJob, newStatus: String, notes: String = "") {
        viewModelScope.launch {
            val updated = job.copy(
                status = newStatus,
                technicianNotes = if (notes.isNotBlank()) "${job.technicianNotes}\n[$newStatus]: $notes" else job.technicianNotes,
                deliveryDate = if (newStatus == "DELIVERED") System.currentTimeMillis() else job.deliveryDate
            )
            repository.updateRepairJob(updated)
        }
    }

    fun deleteRepairJob(job: RepairJob) {
        viewModelScope.launch {
            repository.deleteRepairJob(job)
        }
    }

    // Invoices
    val invoicesList: StateFlow<List<Invoice>> = repository.invoices.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveInvoice(invoice: Invoice, items: List<InvoiceLineItem>) {
        viewModelScope.launch {
            val jsonArray = JSONArray()
            items.forEach { line ->
                val obj = JSONObject().apply {
                    put("productId", line.productId ?: -1L)
                    put("name", line.name)
                    put("qty", line.qty)
                    put("price", line.unitPrice)
                    put("amount", line.lineTotal)
                }
                jsonArray.put(obj)
            }

            val invWithJson = invoice.copy(itemsJson = jsonArray.toString())
            if (invoice.id == 0L) {
                val nextInvNo = if (invoice.type == "QUOTATION") "QT-2026-" + String.format("%03d", invoicesList.value.size + 1)
                else "INV-2026-" + String.format("%03d", invoicesList.value.size + 1)
                repository.insertInvoice(invWithJson.copy(invoiceNo = nextInvNo))

                // Deduct stock for items if sale invoice
                if (invoice.type == "GST_INVOICE") {
                    items.forEach { line ->
                        line.productId?.let { pid ->
                            if (pid > 0) {
                                repository.updateStock(pid, -line.qty)
                                repository.recordStockMovement(
                                    StockMovement(
                                        productId = pid,
                                        productName = line.name,
                                        type = "SALE_OUT",
                                        quantityChange = -line.qty,
                                        referenceNo = nextInvNo,
                                        notes = "Billed to ${invoice.customerName}"
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                repository.updateInvoice(invWithJson)
            }
        }
    }

    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
        }
    }

    // Suppliers
    val suppliersList: StateFlow<List<Supplier>> = repository.suppliers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveSupplier(supplier: Supplier) {
        viewModelScope.launch {
            if (supplier.id == 0L) repository.insertSupplier(supplier) else repository.updateSupplier(supplier)
        }
    }

    fun deleteSupplier(supplier: Supplier) {
        viewModelScope.launch {
            repository.deleteSupplier(supplier)
        }
    }

    // Stock Movement History
    val stockMovementsList: StateFlow<List<StockMovement>> = repository.stockMovements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Warranty Claims
    val warrantyClaimsList: StateFlow<List<WarrantyClaim>> = repository.warrantyClaims.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveWarrantyClaim(claim: WarrantyClaim) {
        viewModelScope.launch {
            if (claim.id == 0L) {
                val nextNo = "WAR-" + (100 + warrantyClaimsList.value.size + 1)
                repository.insertWarrantyClaim(claim.copy(claimNo = nextNo))
            } else {
                repository.updateWarrantyClaim(claim)
            }
        }
    }

    // Helper method to parse Invoice line items JSON string into Kotlin objects
    fun parseInvoiceItems(jsonStr: String): List<InvoiceLineItem> {
        val list = mutableListOf<InvoiceLineItem>()
        if (jsonStr.isBlank()) return list
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val pid = if (obj.has("productId")) obj.getLong("productId") else null
                val name = obj.optString("name", "Service Item")
                val qty = obj.optInt("qty", 1)
                val price = obj.optDouble("price", 0.0)
                val amount = obj.optDouble("amount", qty * price)
                list.add(InvoiceLineItem(productId = if (pid != null && pid > 0) pid else null, name = name, qty = qty, unitPrice = price, lineTotal = amount))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
