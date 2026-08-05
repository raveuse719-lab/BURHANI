package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mobile: String,
    val email: String = "",
    val address: String = "",
    val gstNumber: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val brand: String,
    val category: String, // Laptop, Desktop, Printer, CCTV, Networking, Cartridge, Accessory
    val modelNumber: String = "",
    val serialNumber: String = "",
    val barcode: String = "",
    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val warrantyMonths: Int = 12,
    val stockQuantity: Int = 0,
    val minStockLevel: Int = 2
)

@Entity(tableName = "repair_jobs")
data class RepairJob(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jobNo: String, // e.g., REP-1001
    val customerId: Long,
    val customerName: String,
    val customerMobile: String,
    val productName: String,
    val productCategory: String,
    val brand: String = "",
    val modelNumber: String = "",
    val serialNumber: String = "",
    val problemDescription: String,
    val accessoriesReceived: String = "", // Adapter, Power cable, Ink cartridge, Mouse, Bag, etc.
    val receivedDate: Long = System.currentTimeMillis(),
    val expectedDeliveryDate: Long = System.currentTimeMillis() + (3 * 86400000L),
    val assignedTechnician: String = "Unassigned",
    val status: String = "RECEIVED", // RECEIVED, INSPECTION, REPAIRING, WAITING_PARTS, READY, DELIVERED, RETURNED_NO_REPAIR, WARRANTY_REPAIR
    val repairCost: Double = 0.0,
    val sparePartsUsed: String = "",
    val technicianNotes: String = "",
    val isWarrantyClaim: Boolean = false,
    val deliveryDate: Long? = null
)

@Entity(tableName = "invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNo: String, // e.g. INV-2026-001 or QT-2026-001
    val type: String = "GST_INVOICE", // GST_INVOICE, QUOTATION, REPAIR_BILL
    val customerId: Long,
    val customerName: String,
    val customerMobile: String,
    val customerGst: String = "",
    val customerAddress: String = "",
    val subtotal: Double,
    val taxRate: Double = 18.0, // e.g. 18.0% GST
    val cgstAmount: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val igstAmount: Double = 0.0,
    val discount: Double = 0.0,
    val totalAmount: Double,
    val paymentStatus: String = "PAID", // PAID, PENDING, PARTIAL
    val paymentMethod: String = "CASH", // CASH, UPI, BANK_TRANSFER, CARD, CREDIT
    val date: Long = System.currentTimeMillis(),
    val itemsJson: String = "[]" // JSON string representing list of line items
)

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contactPerson: String = "",
    val mobile: String = "",
    val email: String = "",
    val address: String = "",
    val gstNumber: String = ""
)

@Entity(tableName = "stock_movements")
data class StockMovement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val productName: String,
    val type: String, // PURCHASE_IN, SALE_OUT, REPAIR_USE, ADJUSTMENT
    val quantityChange: Int,
    val referenceNo: String = "",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "warranty_claims")
data class WarrantyClaim(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val claimNo: String,
    val customerId: Long,
    val customerName: String,
    val productName: String,
    val serialNumber: String,
    val purchaseDate: Long,
    val warrantyExpiryDate: Long,
    val claimDate: Long = System.currentTimeMillis(),
    val issueDescription: String,
    val status: String = "SUBMITTED" // SUBMITTED, APPROVED, REPLACED, REJECTED
)

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val businessName: String = "Burhani Infotech",
    val tagline: String = "Computer, Printer, CCTV & Service Center",
    val address: String = "Shop No. 4, Tech Plaza, Station Road",
    val phone: String = "+91 98250 12345",
    val email: String = "info@burhaniinfotech.com",
    val gstin: String = "24ABCDE1234F1Z5",
    val bankName: String = "HDFC Bank Ltd",
    val accountNo: String = "50200088991122",
    val ifscCode: String = "HDFC0001234",
    val upiId: String = "burhaniinfotech@okaxis",
    val terms: String = "1. Goods once sold will not be taken back.\n2. Warranty as per manufacturer terms.\n3. Physical damage & burn void warranty."
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val role: String = "STAFF", // ADMIN, STAFF
    val pin: String = "1234"
)
