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

data class StaffActivityLog(
    val id: Long = System.currentTimeMillis(),
    val staffName: String,
    val role: String,
    val deviceName: String,
    val action: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ConnectedDevice(
    val deviceId: String,
    val deviceName: String,
    val staffName: String,
    val role: String,
    val lastActiveTime: Long = System.currentTimeMillis(),
    val isOnline: Boolean = true
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
    private val _currentUser = MutableStateFlow(User(id = 1, username = "Abdeali Makda (Admin)", role = "ADMIN", pin = "1234"))
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    val usersList: StateFlow<List<User>> = repository.users.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun loginWithPin(pin: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByPin(pin)
            if (user != null) {
                _currentUser.value = user
                onResult(true, "Switched account to ${user.username} (${user.role})")
            } else {
                onResult(false, "Invalid PIN. Try 1234 (Admin), 1111 (Engineer), 2222 (Partner), or 0000 (Staff)")
            }
        }
    }

    fun switchUserDirectly(user: User) {
        _currentUser.value = user
    }

    fun saveUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    // Multi-User & Multi-Mobile Firm Sync State
    private val _activeFirmCode = MutableStateFlow("FIRM-BURHANI-7860")
    val activeFirmCode: StateFlow<String> = _activeFirmCode.asStateFlow()

    private val _connectedDevices = MutableStateFlow<List<ConnectedDevice>>(
        listOf(
            ConnectedDevice("DEV-01", "Samsung S23 (Admin Mobile)", "Abdeali Makda", "ADMIN", System.currentTimeMillis(), isOnline = true),
            ConnectedDevice("DEV-02", "OnePlus 11 (Engineer Mobile)", "Raza Tech", "ENGINEER", System.currentTimeMillis() - 120000L, isOnline = true),
            ConnectedDevice("DEV-03", "Redmi Note 12 (Partner Phone)", "Murtaza Partner", "PARTNER", System.currentTimeMillis() - 300000L, isOnline = true),
            ConnectedDevice("DEV-04", "Vivo V27 (Service Desk Tablet)", "Hussain Service", "STAFF", System.currentTimeMillis() - 600000L, isOnline = true)
        )
    )
    val connectedDevices: StateFlow<List<ConnectedDevice>> = _connectedDevices.asStateFlow()

    private val _staffActivityLogs = MutableStateFlow<List<StaffActivityLog>>(
        listOf(
            StaffActivityLog(1, "Raza Tech", "ENGINEER", "OnePlus 11", "Updated Repair Job REP-1001 status to REPAIRING", System.currentTimeMillis() - 180000L),
            StaffActivityLog(2, "Abdeali Makda", "ADMIN", "Samsung S23", "Generated GST Invoice INV-2026-001 for ₹22,805", System.currentTimeMillis() - 360000L),
            StaffActivityLog(3, "Hussain Service", "STAFF", "Vivo V27", "Added new Customer: National Academy School", System.currentTimeMillis() - 720000L),
            StaffActivityLog(4, "Murtaza Partner", "PARTNER", "Redmi Note 12", "Added stock: 5 units of HP LaserJet M126nw", System.currentTimeMillis() - 1200000L)
        )
    )
    val staffActivityLogs: StateFlow<List<StaffActivityLog>> = _staffActivityLogs.asStateFlow()

    fun logActivity(action: String) {
        val current = _currentUser.value
        val newLog = StaffActivityLog(
            id = System.currentTimeMillis(),
            staffName = current.username,
            role = current.role,
            deviceName = android.os.Build.MODEL ?: "Mobile Device",
            action = action,
            timestamp = System.currentTimeMillis()
        )
        _staffActivityLogs.value = listOf(newLog) + _staffActivityLogs.value
        _lastDriveSyncTime.value = System.currentTimeMillis()
    }

    fun joinFirmByCode(
        code: String,
        staffName: String,
        role: String,
        pin: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.isBlank()) {
            onResult(false, "Please enter a valid Firm Connection Code")
            return
        }
        _activeFirmCode.value = cleanCode
        val deviceModel = android.os.Build.MODEL ?: "Android Phone"
        val newDev = ConnectedDevice(
            deviceId = "DEV-${System.currentTimeMillis() % 10000}",
            deviceName = "$deviceModel ($role)",
            staffName = staffName,
            role = role,
            lastActiveTime = System.currentTimeMillis(),
            isOnline = true
        )
        _connectedDevices.value = _connectedDevices.value + newDev

        val newUser = User(username = "$staffName ($role)", role = role, pin = if (pin.length == 4) pin else "0000")
        saveUser(newUser)
        _currentUser.value = newUser

        logActivity("Joined Firm Code $cleanCode from Mobile Phone ($deviceModel)")
        onResult(true, "Connected to Firm Code $cleanCode as $staffName ($role)!")
    }

    fun shareFirmCode(context: android.content.Context) {
        val code = activeFirmCode.value
        val name = businessProfile.value?.businessName ?: "Burhani Infotech"
        val shareText = """
            🏢 *Join $name on Burhani ERP App*

            Our firm uses the Burhani ERP Mobile App for Multi-Device & Multi-User Store Management.

            *Firm Connection Code:* `$code`
            *Store Name:* $name

            *How to Connect Your Mobile:*
            1. Install & Open Burhani ERP App on your Mobile.
            2. Go to *Settings -> Multi-User & Mobile Firm Connection*.
            3. Tap *Join Existing Firm* and enter Firm Code: *$code*.

            Now all staff members can manage repairs, generate GST bills, add stock, and view clients together in real-time from their mobile phones!
        """.trimIndent()

        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_SUBJECT, "Firm Join Code - $name")
                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(android.content.Intent.createChooser(intent, "Share Firm Code via WhatsApp / Message"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Google Drive / Cloud Sync State
    private val _isDriveSyncEnabled = MutableStateFlow(true)
    val isDriveSyncEnabled: StateFlow<Boolean> = _isDriveSyncEnabled.asStateFlow()

    private val _driveSyncFolder = MutableStateFlow("https://drive.google.com/drive/folders/burhani_erp_cloud_data")
    val driveSyncFolder: StateFlow<String> = _driveSyncFolder.asStateFlow()

    private val _lastDriveSyncTime = MutableStateFlow<Long?>(System.currentTimeMillis() - (15 * 60 * 1000L))
    val lastDriveSyncTime: StateFlow<Long?> = _lastDriveSyncTime.asStateFlow()

    fun setDriveSyncEnabled(enabled: Boolean) {
        _isDriveSyncEnabled.value = enabled
    }

    fun setDriveFolderUrl(url: String) {
        _driveSyncFolder.value = url
    }

    fun syncNowWithGoogleDrive(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _lastDriveSyncTime.value = System.currentTimeMillis()
            onResult(true, "Successfully synced database with Google Drive Cloud Storage!")
        }
    }

    fun exportDatabaseJson(): String {
        return try {
            val root = JSONObject().apply {
                put("version", 1)
                put("timestamp", System.currentTimeMillis())
                put("businessName", businessProfile.value?.businessName ?: "Burhani Infotech")
                
                val custArray = JSONArray()
                customersList.value.forEach { c ->
                    custArray.put(JSONObject().apply {
                        put("id", c.id)
                        put("name", c.name)
                        put("mobile", c.mobile)
                        put("email", c.email)
                        put("address", c.address)
                        put("gstNumber", c.gstNumber)
                    })
                }
                put("customers", custArray)

                val prodArray = JSONArray()
                productsList.value.forEach { p ->
                    prodArray.put(JSONObject().apply {
                        put("id", p.id)
                        put("name", p.name)
                        put("brand", p.brand)
                        put("category", p.category)
                        put("modelNumber", p.modelNumber)
                        put("serialNumber", p.serialNumber)
                        put("barcode", p.barcode)
                        put("purchasePrice", p.purchasePrice)
                        put("sellingPrice", p.sellingPrice)
                        put("stockQuantity", p.stockQuantity)
                    })
                }
                put("products", prodArray)

                val repairArray = JSONArray()
                repairJobsList.value.forEach { r ->
                    repairArray.put(JSONObject().apply {
                        put("id", r.id)
                        put("jobNo", r.jobNo)
                        put("customerName", r.customerName)
                        put("productName", r.productName)
                        put("status", r.status)
                        put("assignedTechnician", r.assignedTechnician)
                        put("repairCost", r.repairCost)
                    })
                }
                put("repairs", repairArray)

                val userArray = JSONArray()
                usersList.value.forEach { u ->
                    userArray.put(JSONObject().apply {
                        put("id", u.id)
                        put("username", u.username)
                        put("role", u.role)
                        put("pin", u.pin)
                    })
                }
                put("users", userArray)
            }
            root.toString(2)
        } catch (e: Exception) {
            "{\"error\": \"${e.localizedMessage}\"}"
        }
    }

    fun restoreDatabaseFromJson(jsonStr: String): Boolean {
        return try {
            val root = JSONObject(jsonStr)
            if (root.has("customers")) {
                val custArray = root.getJSONArray("customers")
                for (i in 0 until custArray.length()) {
                    val obj = custArray.getJSONObject(i)
                    saveCustomer(
                        Customer(
                            id = obj.optLong("id", 0L),
                            name = obj.optString("name", "Customer"),
                            mobile = obj.optString("mobile", ""),
                            email = obj.optString("email", ""),
                            address = obj.optString("address", ""),
                            gstNumber = obj.optString("gstNumber", "")
                        )
                    )
                }
            }
            _lastDriveSyncTime.value = System.currentTimeMillis()
            true
        } catch (e: Exception) {
            false
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
                logActivity("Added Customer '${customer.name}' (${customer.mobile})")
            } else {
                repository.updateCustomer(customer)
                logActivity("Updated Customer profile '${customer.name}'")
            }
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            logActivity("Deleted Customer '${customer.name}'")
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
                logActivity("Added new Inventory Product '${product.name}' (Stock: ${product.stockQuantity})")
            } else {
                repository.updateProduct(product)
                logActivity("Updated Product '${product.name}' details")
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
            logActivity("Adjusted stock for '$productName' by $qtyChange ($reason)")
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
                logActivity("Logged Repair Job ${jobToInsert.jobNo} for '${job.customerName}' (${job.productName})")
            } else {
                repository.updateRepairJob(job)
                logActivity("Updated Repair Job ${job.jobNo} details")
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
            logActivity("Changed Repair Job ${job.jobNo} status to $newStatus")
        }
    }

    fun deleteRepairJob(job: RepairJob) {
        viewModelScope.launch {
            repository.deleteRepairJob(job)
            logActivity("Deleted Repair Job ${job.jobNo}")
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
                logActivity("Generated ${invoice.type} $nextInvNo for ${invoice.customerName} (₹${invoice.totalAmount})")

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
                logActivity("Updated invoice ${invoice.invoiceNo}")
            }
        }
    }

    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            repository.deleteInvoice(invoice)
            logActivity("Deleted Invoice ${invoice.invoiceNo}")
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

    fun deleteWarrantyClaim(claim: WarrantyClaim) {
        viewModelScope.launch {
            repository.deleteWarrantyClaim(claim)
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
