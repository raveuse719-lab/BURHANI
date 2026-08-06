package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Customer::class,
        Product::class,
        RepairJob::class,
        Invoice::class,
        Supplier::class,
        StockMovement::class,
        WarrantyClaim::class,
        BusinessProfile::class,
        User::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun productDao(): ProductDao
    abstract fun repairJobDao(): RepairJobDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun supplierDao(): SupplierDao
    abstract fun stockMovementDao(): StockMovementDao
    abstract fun warrantyClaimDao(): WarrantyClaimDao
    abstract fun businessProfileDao(): BusinessProfileDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "burhani_infotech_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            // Profile
            db.businessProfileDao().insertOrUpdateProfile(
                BusinessProfile(
                    id = 1,
                    businessName = "Burhani Infotech",
                    tagline = "Sales, Service & Repair Solutions",
                    address = "102 Royal Commerce Hub, Station Road, City",
                    phone = "+91 98250 12345",
                    email = "support@burhaniinfotech.com",
                    gstin = "24ABCDB1234F1Z5",
                    bankName = "HDFC Bank",
                    accountNo = "50200088991122",
                    ifscCode = "HDFC0001234",
                    upiId = "burhaniinfotech@okaxis"
                )
            )

            // Users (Admin, Engineer, Partner, Staff)
            db.userDao().insertUser(User(username = "Abdeali Makda (Admin)", role = "ADMIN", pin = "1234"))
            db.userDao().insertUser(User(username = "Raza Tech (Engineer)", role = "ENGINEER", pin = "1111"))
            db.userDao().insertUser(User(username = "Murtaza Partner (Partner)", role = "PARTNER", pin = "2222"))
            db.userDao().insertUser(User(username = "Hussain Service (Staff)", role = "STAFF", pin = "0000"))

            // Customers
            val c1 = db.customerDao().insertCustomer(Customer(name = "Rajesh Sharma", mobile = "9876543210", email = "rajesh.sharma@email.com", address = "A-301 Green Towers, City", gstNumber = "24AAACR1234A1Z1"))
            val c2 = db.customerDao().insertCustomer(Customer(name = "Mehta & Co. Traders", mobile = "9988776655", email = "info@mehtatraders.in", address = "45 Commercial Complex, Station Rd", gstNumber = "24AABCM9876K1Z9"))
            val c3 = db.customerDao().insertCustomer(Customer(name = "Priya Patel", mobile = "9723456789", email = "priya.p@gmail.com", address = "78 Sunshine Society", gstNumber = ""))
            val c4 = db.customerDao().insertCustomer(Customer(name = "National Academy School", mobile = "9824055443", email = "contact@nationalacademy.edu", address = "School Circle, Main Road", gstNumber = "24AAAAA0000A1Z5"))

            // Products
            val p1 = db.productDao().insertProduct(Product(name = "HP LaserJet Pro M126nw Printer", brand = "HP", category = "Printer", modelNumber = "M126nw", serialNumber = "CNB123456", barcode = "889296001122", purchasePrice = 14500.0, sellingPrice = 17800.0, warrantyMonths = 12, stockQuantity = 5, minStockLevel = 2))
            val p2 = db.productDao().insertProduct(Product(name = "Epson EcoTank L3210 All-in-One", brand = "Epson", category = "Printer", modelNumber = "L3210", serialNumber = "EPS789012", barcode = "889296003344", purchasePrice = 11800.0, sellingPrice = 14200.0, warrantyMonths = 12, stockQuantity = 1, minStockLevel = 3)) // Low stock alert!
            val p3 = db.productDao().insertProduct(Product(name = "Dell Vostro 15 i5 12th Gen Laptop", brand = "Dell", category = "Laptop", modelNumber = "Vostro 3520", serialNumber = "DELL998877", barcode = "889296005566", purchasePrice = 44000.0, sellingPrice = 51500.0, warrantyMonths = 12, stockQuantity = 3, minStockLevel = 2))
            val p4 = db.productDao().insertProduct(Product(name = "Hikvision 2MP Dome CCTV Camera", brand = "Hikvision", category = "CCTV", modelNumber = "DS-2CE56D0T-IRF", serialNumber = "HIK332211", barcode = "889296007788", purchasePrice = 1200.0, sellingPrice = 1650.0, warrantyMonths = 24, stockQuantity = 15, minStockLevel = 5))
            val p5 = db.productDao().insertProduct(Product(name = "TP-Link 300Mbps Wi-Fi Router", brand = "TP-Link", category = "Networking", modelNumber = "TL-WR841N", serialNumber = "TPL445566", barcode = "889296009900", purchasePrice = 950.0, sellingPrice = 1350.0, warrantyMonths = 36, stockQuantity = 8, minStockLevel = 3))
            val p6 = db.productDao().insertProduct(Product(name = "Kingston 240GB 2.5\" SSD", brand = "Kingston", category = "Accessory", modelNumber = "A400", serialNumber = "KNG112233", barcode = "889296011122", purchasePrice = 1400.0, sellingPrice = 1950.0, warrantyMonths = 36, stockQuantity = 12, minStockLevel = 4))
            val p7 = db.productDao().insertProduct(Product(name = "HP 88A Black Laser Cartridge", brand = "HP", category = "Cartridge", modelNumber = "CC388A", serialNumber = "HPC88A01", barcode = "889296022233", purchasePrice = 850.0, sellingPrice = 1250.0, warrantyMonths = 6, stockQuantity = 0, minStockLevel = 2)) // Low stock alert!

            // Suppliers
            val sup1 = db.supplierDao().insertSupplier(Supplier(name = "Supertron Electronics Ltd", contactPerson = "Amit Verma", mobile = "9811002233", email = "sales@supertronindia.com", address = "Tech Hub Zone, City", gstNumber = "24AAACS1122K1Z8"))
            val sup2 = db.supplierDao().insertSupplier(Supplier(name = "Ingram Micro India", contactPerson = "Sandeep Gupta", mobile = "9822334455", email = "orders@ingrammicro.in", address = "Logistics Park, Phase 2", gstNumber = "24AABCI5566P1Z3"))

            // Repair Jobs
            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            db.repairJobDao().insertRepairJob(
                RepairJob(
                    jobNo = "REP-1001",
                    customerId = c1,
                    customerName = "Rajesh Sharma",
                    customerMobile = "9876543210",
                    productName = "HP Pavilion Laptop 14-ce",
                    productCategory = "Laptop",
                    brand = "HP",
                    modelNumber = "14-ce3065TU",
                    serialNumber = "5CD0123XYZ",
                    problemDescription = "Display not turning on, power LED blinking. Customer reports water spill.",
                    accessoriesReceived = "Original HP 65W Power Adapter, Laptop Bag",
                    receivedDate = now - (2 * dayMs),
                    expectedDeliveryDate = now + dayMs,
                    assignedTechnician = "Abdeali Tech",
                    status = "REPAIRING",
                    repairCost = 2800.0,
                    sparePartsUsed = "Display Cable replaced, Motherboard Serviced",
                    technicianNotes = "Cleaned corrosion near DC jack. Screen cable re-seated."
                )
            )

            db.repairJobDao().insertRepairJob(
                RepairJob(
                    jobNo = "REP-1002",
                    customerId = c2,
                    customerName = "Mehta & Co. Traders",
                    customerMobile = "9988776655",
                    productName = "Epson L3150 Wi-Fi InkTank Printer",
                    productCategory = "Printer",
                    brand = "Epson",
                    modelNumber = "L3150",
                    serialNumber = "EPS554433",
                    problemDescription = "Paper jam error continuously flashing. Black ink not printing clearly.",
                    accessoriesReceived = "Power Cord, USB Cable",
                    receivedDate = now - (1 * dayMs),
                    expectedDeliveryDate = now + (2 * dayMs),
                    assignedTechnician = "Hussain Service",
                    status = "WAITING_PARTS",
                    repairCost = 1500.0,
                    sparePartsUsed = "Epson Black Printhead Dampers",
                    technicianNotes = "Ordered replacement damper unit from supplier."
                )
            )

            db.repairJobDao().insertRepairJob(
                RepairJob(
                    jobNo = "REP-1003",
                    customerId = c3,
                    customerName = "Priya Patel",
                    customerMobile = "9723456789",
                    productName = "Dell Inspiron 3511 Laptop",
                    productCategory = "Laptop",
                    brand = "Dell",
                    modelNumber = "3511",
                    serialNumber = "DEL778899",
                    problemDescription = "Very slow performance, Windows booting takes 10 minutes.",
                    accessoriesReceived = "Adapter only",
                    receivedDate = now - (3 * dayMs),
                    expectedDeliveryDate = now - (1 * dayMs),
                    assignedTechnician = "Abdeali Tech",
                    status = "READY",
                    repairCost = 2200.0,
                    sparePartsUsed = "240GB Kingston NVMe SSD added",
                    technicianNotes = "Cloned OS to SSD. Boot time reduced to 12 seconds."
                )
            )

            db.repairJobDao().insertRepairJob(
                RepairJob(
                    jobNo = "REP-1004",
                    customerId = c4,
                    customerName = "National Academy School",
                    customerMobile = "9824055443",
                    productName = "Canon imageRUNNER 2206 Copier",
                    productCategory = "Printer",
                    brand = "Canon",
                    modelNumber = "iR2206",
                    serialNumber = "CAN990011",
                    problemDescription = "E000 error code displaying. Fuser roller temperature fault.",
                    accessoriesReceived = "Power cord attached",
                    receivedDate = now - (5 * dayMs),
                    expectedDeliveryDate = now - (2 * dayMs),
                    assignedTechnician = "Hussain Service",
                    status = "DELIVERED",
                    repairCost = 4500.0,
                    sparePartsUsed = "Fuser Film Sleeve & Thermistor",
                    deliveryDate = now - (1 * dayMs),
                    technicianNotes = "Replaced fuser film. Tested 100 pages printing OK."
                )
            )

            // Invoices
            val sampleItemsJson = """
                [
                  {"productId":$p1,"name":"HP LaserJet Pro M126nw Printer","qty":1,"price":17800.0,"amount":17800.0},
                  {"productId":$p6,"name":"Kingston 240GB SSD","qty":1,"price":1950.0,"amount":1950.0}
                ]
            """.trimIndent()

            db.invoiceDao().insertInvoice(
                Invoice(
                    invoiceNo = "INV-2026-001",
                    type = "GST_INVOICE",
                    customerId = c1,
                    customerName = "Rajesh Sharma",
                    customerMobile = "9876543210",
                    customerGst = "24AAACR1234A1Z1",
                    customerAddress = "A-301 Green Towers, City",
                    subtotal = 19750.0,
                    taxRate = 18.0,
                    cgstAmount = 1777.5,
                    sgstAmount = 1777.5,
                    igstAmount = 0.0,
                    discount = 500.0,
                    totalAmount = 22805.0,
                    paymentStatus = "PAID",
                    paymentMethod = "UPI",
                    date = now - (4 * dayMs),
                    itemsJson = sampleItemsJson
                )
            )

            val quoteItemsJson = """
                [
                  {"productId":$p4,"name":"Hikvision 2MP Dome CCTV Camera","qty":8,"price":1650.0,"amount":13200.0},
                  {"productId":$p5,"name":"TP-Link Wi-Fi Router","qty":2,"price":1350.0,"amount":2700.0}
                ]
            """.trimIndent()

            db.invoiceDao().insertInvoice(
                Invoice(
                    invoiceNo = "QT-2026-012",
                    type = "QUOTATION",
                    customerId = c4,
                    customerName = "National Academy School",
                    customerMobile = "9824055443",
                    customerGst = "24AAAAA0000A1Z5",
                    customerAddress = "School Circle, Main Road",
                    subtotal = 15900.0,
                    taxRate = 18.0,
                    cgstAmount = 1431.0,
                    sgstAmount = 1431.0,
                    igstAmount = 0.0,
                    discount = 900.0,
                    totalAmount = 17862.0,
                    paymentStatus = "PENDING",
                    paymentMethod = "CREDIT",
                    date = now - (1 * dayMs),
                    itemsJson = quoteItemsJson
                )
            )

            // Warranty Claims
            db.warrantyClaimDao().insertWarrantyClaim(
                WarrantyClaim(
                    claimNo = "WAR-101",
                    customerId = c2,
                    customerName = "Mehta & Co. Traders",
                    productName = "HP LaserJet Pro M126nw Printer",
                    serialNumber = "CNB123456",
                    purchaseDate = now - (120 * dayMs),
                    warrantyExpiryDate = now + (245 * dayMs),
                    issueDescription = "Scanner glass light flickering during ADF scanning",
                    status = "APPROVED"
                )
            )
        }
    }
}
