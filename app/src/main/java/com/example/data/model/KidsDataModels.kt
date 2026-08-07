package com.example.data.model

data class AbcItem(
    val letter: String,
    val wordEn: String,
    val wordHi: String,
    val wordGu: String,
    val icon: String,
    val colorHex: Long,
    val funFactEn: String,
    val funFactHi: String,
    val funFactGu: String
)

data class NumberItem(
    val number: Int,
    val wordEn: String,
    val wordHi: String,
    val wordGu: String,
    val emoji: String,
    val colorHex: Long
)

data class ColorItem(
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val hexValue: Long,
    val exampleObjectEn: String,
    val exampleObjectHi: String,
    val exampleObjectGu: String,
    val exampleEmoji: String
)

data class ShapeItem(
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val icon: String,
    val sides: Int,
    val exampleObjectEn: String,
    val exampleObjectHi: String,
    val exampleObjectGu: String
)

data class AnimalItem(
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val imageEmoji: String,
    val soundText: String,
    val category: String, // "Wild", "Farm", "Pet"
    val funFactEn: String,
    val funFactHi: String,
    val funFactGu: String
)

data class BirdItem(
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val imageEmoji: String,
    val soundText: String
)

data class FruitVegItem(
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val imageEmoji: String,
    val isFruit: Boolean,
    val color: String
)

data class VehicleItem(
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val imageEmoji: String,
    val soundText: String,
    val type: String // "Land", "Air", "Water"
)

data class RhymeItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val titleGu: String,
    val language: String, // "en", "hi", "gu"
    val lyrics: List<String>,
    val icon: String,
    val bgGradient: LongArray
)

data class QuizQuestion(
    val id: Int,
    val category: String, // "ABC", "Numbers", "Animals", "Colors"
    val questionEn: String,
    val questionHi: String,
    val questionGu: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationEn: String
)

data class ColoringPageTemplate(
    val id: String,
    val title: String,
    val category: String,
    val svgIcon: String,
    val svgPathData: String
)

data class JigsawPuzzleData(
    val id: String,
    val title: String,
    val emoji: String,
    val category: String,
    val colorHex: Long
)
