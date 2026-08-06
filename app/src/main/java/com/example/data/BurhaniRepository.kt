package com.example.data

import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

class BurhaniRepository(private val db: AppDatabase) {

    // Customer operations
    val customers: Flow<List<Customer>> = db.customerDao().getAllCustomers()
    fun searchCustomers(query: String): Flow<List<Customer>> = db.customerDao().searchCustomers(query)
    fun getCustomerById(id: Long): Flow<Customer?> = db.customerDao().getCustomerById(id)
    suspend fun insertCustomer(customer: Customer): Long = db.customerDao().insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = db.customerDao().updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = db.customerDao().deleteCustomer(customer)

    // Product & Stock operations
    val products: Flow<List<Product>> = db.productDao().getAllProducts()
    val lowStockProducts: Flow<List<Product>> = db.productDao().getLowStockProducts()
    fun searchProducts(query: String): Flow<List<Product>> = db.productDao().searchProducts(query)
    fun getProductById(id: Long): Flow<Product?> = db.productDao().getProductById(id)
    suspend fun getProductByBarcode(barcode: String): Product? = db.productDao().getProductByBarcode(barcode)
    suspend fun insertProduct(product: Product): Long = db.productDao().insertProduct(product)
    suspend fun updateProduct(product: Product) = db.productDao().updateProduct(product)
    suspend fun updateStock(id: Long, qtyChange: Int) = db.productDao().updateStock(id, qtyChange)
    suspend fun deleteProduct(product: Product) = db.productDao().deleteProduct(product)

    // Repair Job operations
    val repairJobs: Flow<List<RepairJob>> = db.repairJobDao().getAllRepairJobs()
    fun getRepairJobsByStatus(status: String): Flow<List<RepairJob>> = db.repairJobDao().getRepairJobsByStatus(status)
    fun getRepairJobsByCustomer(customerId: Long): Flow<List<RepairJob>> = db.repairJobDao().getRepairJobsByCustomer(customerId)
    fun getRepairJobsBySerial(serial: String): Flow<List<RepairJob>> = db.repairJobDao().getRepairJobsBySerial(serial)
    fun getRepairJobById(id: Long): Flow<RepairJob?> = db.repairJobDao().getRepairJobById(id)
    suspend fun insertRepairJob(job: RepairJob): Long = db.repairJobDao().insertRepairJob(job)
    suspend fun updateRepairJob(job: RepairJob) = db.repairJobDao().updateRepairJob(job)
    suspend fun deleteRepairJob(job: RepairJob) = db.repairJobDao().deleteRepairJob(job)

    // Invoice & Billing operations
    val invoices: Flow<List<Invoice>> = db.invoiceDao().getAllInvoices()
    fun getInvoicesByCustomer(customerId: Long): Flow<List<Invoice>> = db.invoiceDao().getInvoicesByCustomer(customerId)
    fun getInvoiceById(id: Long): Flow<Invoice?> = db.invoiceDao().getInvoiceById(id)
    suspend fun insertInvoice(invoice: Invoice): Long = db.invoiceDao().insertInvoice(invoice)
    suspend fun updateInvoice(invoice: Invoice) = db.invoiceDao().updateInvoice(invoice)
    suspend fun deleteInvoice(invoice: Invoice) = db.invoiceDao().deleteInvoice(invoice)

    // Suppliers
    val suppliers: Flow<List<Supplier>> = db.supplierDao().getAllSuppliers()
    suspend fun insertSupplier(supplier: Supplier): Long = db.supplierDao().insertSupplier(supplier)
    suspend fun updateSupplier(supplier: Supplier) = db.supplierDao().updateSupplier(supplier)
    suspend fun deleteSupplier(supplier: Supplier) = db.supplierDao().deleteSupplier(supplier)

    // Stock Movements
    val stockMovements: Flow<List<StockMovement>> = db.stockMovementDao().getAllStockMovements()
    suspend fun recordStockMovement(movement: StockMovement): Long = db.stockMovementDao().insertStockMovement(movement)

    // Warranty Claims
    val warrantyClaims: Flow<List<WarrantyClaim>> = db.warrantyClaimDao().getAllWarrantyClaims()
    suspend fun insertWarrantyClaim(claim: WarrantyClaim): Long = db.warrantyClaimDao().insertWarrantyClaim(claim)
    suspend fun updateWarrantyClaim(claim: WarrantyClaim) = db.warrantyClaimDao().updateWarrantyClaim(claim)

    // Business Profile
    val businessProfile: Flow<BusinessProfile?> = db.businessProfileDao().getBusinessProfile()
    suspend fun updateBusinessProfile(profile: BusinessProfile) = db.businessProfileDao().insertOrUpdateProfile(profile)

    // Users & Roles
    val users: Flow<List<User>> = db.userDao().getAllUsers()
    suspend fun getUserByPin(pin: String): User? = db.userDao().getUserByPin(pin)
    suspend fun insertUser(user: User): Long = db.userDao().insertUser(user)
    suspend fun deleteUser(user: User) = db.userDao().deleteUser(user)
}
