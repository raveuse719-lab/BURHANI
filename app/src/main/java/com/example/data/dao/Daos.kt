package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchCustomers(query: String): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    fun getCustomerById(id: Long): Flow<Customer?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR modelNumber LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%' OR serialNumber LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProducts(query: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getProductByBarcode(barcode: String): Product?

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Long): Flow<Product?>

    @Query("SELECT * FROM products WHERE stockQuantity <= minStockLevel ORDER BY stockQuantity ASC")
    fun getLowStockProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :qtyChange WHERE id = :id")
    suspend fun updateStock(id: Long, qtyChange: Int)

    @Delete
    suspend fun deleteProduct(product: Product)
}

@Dao
interface RepairJobDao {
    @Query("SELECT * FROM repair_jobs ORDER BY receivedDate DESC")
    fun getAllRepairJobs(): Flow<List<RepairJob>>

    @Query("SELECT * FROM repair_jobs WHERE status = :status ORDER BY receivedDate DESC")
    fun getRepairJobsByStatus(status: String): Flow<List<RepairJob>>

    @Query("SELECT * FROM repair_jobs WHERE customerId = :customerId ORDER BY receivedDate DESC")
    fun getRepairJobsByCustomer(customerId: Long): Flow<List<RepairJob>>

    @Query("SELECT * FROM repair_jobs WHERE id = :id")
    fun getRepairJobById(id: Long): Flow<RepairJob?>

    @Query("SELECT * FROM repair_jobs WHERE serialNumber = :serialNumber ORDER BY receivedDate DESC")
    fun getRepairJobsBySerial(serialNumber: String): Flow<List<RepairJob>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepairJob(job: RepairJob): Long

    @Update
    suspend fun updateRepairJob(job: RepairJob)

    @Delete
    suspend fun deleteRepairJob(job: RepairJob)
}

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY date DESC")
    fun getAllInvoices(): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE customerId = :customerId ORDER BY date DESC")
    fun getInvoicesByCustomer(customerId: Long): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE id = :id")
    fun getInvoiceById(id: Long): Flow<Invoice?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Delete
    suspend fun deleteInvoice(invoice: Invoice)
}

@Dao
interface SupplierDao {
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<Supplier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)
}

@Dao
interface StockMovementDao {
    @Query("SELECT * FROM stock_movements ORDER BY timestamp DESC")
    fun getAllStockMovements(): Flow<List<StockMovement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockMovement(movement: StockMovement): Long
}

@Dao
interface WarrantyClaimDao {
    @Query("SELECT * FROM warranty_claims ORDER BY claimDate DESC")
    fun getAllWarrantyClaims(): Flow<List<WarrantyClaim>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarrantyClaim(claim: WarrantyClaim): Long

    @Update
    suspend fun updateWarrantyClaim(claim: WarrantyClaim)

    @Delete
    suspend fun deleteWarrantyClaim(claim: WarrantyClaim)
}

@Dao
interface BusinessProfileDao {
    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    fun getBusinessProfile(): Flow<BusinessProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: BusinessProfile)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY username ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE pin = :pin LIMIT 1")
    suspend fun getUserByPin(pin: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)
}
