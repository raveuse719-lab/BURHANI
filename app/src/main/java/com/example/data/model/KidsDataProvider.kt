package com.example.data.model

object KidsDataProvider {

    val abcList = listOf(
        AbcItem("A", "Apple", "सेब", "સફરજન", "🍎", 0xFFFF5252, "An apple a day keeps the doctor away!", "एक सेब रोज खाओ, सेहत बनाओ!", "દરરોજ એક સફરજન ખાવાથી સ્વાસ્થ્ય સારું રહે છે!"),
        AbcItem("B", "Ball", "गेंद", "દડો", "⚽", 0xFFFF9100, "Balls bounce high and roll fast!", "गेंद उछलती है और गोल होती है!", "દડો ખૂબ ઊંચો ઊછળે છે!"),
        AbcItem("C", "Cat", "बिल्ली", "બિલાડી", "🐱", 0xFFFFD600, "Cats say Meow and love milk!", "बिल्ली म्याऊं करती है!", "બિલાડી મ્યાઉં બોલે છે!"),
        AbcItem("D", "Dog", "कुत्ता", "કૂતરો", "🐶", 0xFF00E676, "Dogs are man's best loyal friends!", "कुत्ता वफादार जानवर है!", "કૂતરો વફાદાર પ્રાણી છે!"),
        AbcItem("E", "Elephant", "हाथी", "હાથી", "🐘", 0xFF00B0FF, "Elephants have large trunks and huge ears!", "हाथी के बड़े कान और सूंड होती है!", "હાથીને મોટી સૂંઢ અને મોટા કાન હોય છે!"),
        AbcItem("F", "Fish", "मछली", "માછલી", "🐟", 0xFF2979FF, "Fish swim gracefully in water!", "मछली जल की रानी है!", "માછલી પાણીમાં તરે છે!"),
        AbcItem("G", "Giraffe", "जिराफ", "જિરાફ", "🦒", 0xFF651FFF, "Giraffes have very long necks!", "जिराफ की गर्दन बहुत लंबी होती है!", "જિરાફની ડોક ખૂબ જ લાંબી હોય છે!"),
        AbcItem("H", "House", "घर", "ઘર", "🏠", 0xFFAA00FF, "House is a warm sweet home!", "घर में हम सुरक्षित रहते हैं!", "ઘરમાં આપણે બધા સાથે રહીએ છીએ!"),
        AbcItem("I", "Ice Cream", "आइसक्रीम", "આઈસ્ક્રીમ", "🍦", 0xFFFF4081, "Ice cream is cold and yummy!", "आइसक्रीम ठंडी और मीठी होती है!", "આઈસ્ક્રીમ ઠંડી અને મીઠી હોય છે!"),
        AbcItem("J", "Juice", "जूस", "જ્યુસ", "🧃", 0xFFFF5252, "Juice gives us energy and vitamins!", "ताजा जूस सेहत के लिए अच्छा है!", "તાજો જ્યુસ શક્તિ આપે છે!"),
        AbcItem("K", "Kite", "पतंग", "પતંગ", "🪁", 0xFFFF9100, "Kites fly high in the blue sky!", "पतंग आसमान में उड़ती है!", "પતંગ આકાશમાં ઊંચે ઊડે છે!"),
        AbcItem("L", "Lion", "शेर", "સિંહ", "🦁", 0xFFFFD600, "Lion is the mighty king of the jungle!", "शेर जंगल का राजा है!", "સિંહ જંગલનો રાજા છે!"),
        AbcItem("M", "Monkey", "बंदर", "વાંદરો", "🐒", 0xFF00E676, "Monkeys love jumping on trees and eating bananas!", "बंदर पेड़ पर उछलते हैं!", "વાંદરાઓને કેળાં બહુ ભાવે છે!"),
        AbcItem("N", "Nest", "घोंसला", "માળવો", "🪹", 0xFF00B0FF, "Birds build cozy nests for baby chicks!", "चिड़िया घोंसले में अंडे देती है!", "પક્ષીઓ માળામાં રહે છે!"),
        AbcItem("O", "Owl", "उल्लू", "ઘુવડ", "🦉", 0xFF2979FF, "Owls stay awake at night!", "उल्लू रात में जागता है!", "ઘુવડ રાત્રે જોઈ શકે છે!"),
        AbcItem("P", "Panda", "पांडा", "પાંડા", "🐼", 0xFF651FFF, "Pandas love bamboo leaves!", "पांडा बांस खाता है!", "પાંડા વાંસના પાંદડાં ખાય છે!"),
        AbcItem("Q", "Queen", "रानी", "રાણી", "👑", 0xFFAA00FF, "Queen wears a shiny golden crown!", "रानी के सिर पर मुकुट होता है!", "રાણી સોનાનો મુગટ પહેરે છે!"),
        AbcItem("R", "Rabbit", "खरगोश", "સસલું", "🐰", 0xFFFF4081, "Rabbits hop fast and eat crunchy carrots!", "खरगोश गाजर खाता है!", "સસલું ગાજર ખાય છે!"),
        AbcItem("S", "Sun", "सूरज", "સૂર્ય", "☀️", 0xFFFF5252, "Sun gives bright light and warm shine!", "सूरज हमें रोशनी देता है!", "સૂર્ય આપણને પ્રકાશ આપે છે!"),
        AbcItem("T", "Tiger", "बाघ", "વાઘ", "🐯", 0xFFFF9100, "Tigers have orange coats with dark stripes!", "बाघ हमारा राष्ट्रीय पशु है!", "વાઘ શક્તિશાળી પ્રાણી છે!"),
        AbcItem("U", "Umbrella", "छाता", "છત્રી", "☂️", 0xFFFFD600, "Umbrellas keep us dry in rainfall!", "छाता बारिश में बचाती है!", "છત્રી વરસાદથી બચાવે છે!"),
        AbcItem("V", "Violin", "वायलिन", "વાયલિન", "🎻", 0xFF00E676, "Violin produces melodious musical tunes!", "वायलिन से मीठा संगीत बजता है!", "વાયલિન મધુર સંગીત રેડે છે!"),
        AbcItem("W", "Watermelon", "तरबूज", "તરબૂચ", "🍉", 0xFF00B0FF, "Watermelon is sweet, juicy and refreshing!", "तरबूज में खूब पानी होता है!", "તરબૂચ ખૂબ મીઠું અને રસદાર હોય છે!"),
        AbcItem("X", "Xylophone", "जाइलोफोन", "ઝાયલોફોન", "🎼", 0xFF2979FF, "Xylophone plays colorful musical notes!", "जाइलोफोन से रंगीन धुन बजती है!", "ઝાયલોફોન સંગીતનું સાધન છે!"),
        AbcItem("Y", "Yak", "याक", "યાક", "🐂", 0xFF651FFF, "Yaks live high on cold snowy mountains!", "याक पहाड़ों पर पाया जाता है!", "યાક બરફીલા પર્વતો પર રહે છે!"),
        AbcItem("Z", "Zebra", "जेब्रा", "ઝીબ્રા", "🦓", 0xFFAA00FF, "Zebras have beautiful black and white stripes!", "जेब्रा की सफेद-काली धारियां होती हैं!", "ઝીબ્રા પર કાળા-સફેદ પટ્ટા હોય છે!")
    )

    fun getNumbersList(): List<NumberItem> {
        val emojis = listOf("🍎", "⭐️", "🎈", "🚗", "🌸", "🧸", "⚽", "🐱", "🐥", "🍬")
        val colors = listOf(0xFFFF5252, 0xFFFF9100, 0xFFFFD600, 0xFF00E676, 0xFF00B0FF, 0xFF651FFF, 0xFFFF4081)
        val hindiDigits = arrayOf("शून्य", "एक", "दो", "तीन", "चार", "पांच", "छह", "सात", "आठ", "नौ", "दस")
        val gujaratiDigits = arrayOf("શૂન્ય", "એક", "બે", "ત્રણ", "ચાર", "પાંચ", "છ", "સાત", "આઠ", "નવ", "દસ")

        val list = mutableListOf<NumberItem>()
        for (i in 1..100) {
            val emoji = emojis[(i - 1) % emojis.size]
            val color = colors[(i - 1) % colors.size]
            val wordEn = "Number $i"
            val wordHi = if (i <= 10) hindiDigits[i] else "संख्या $i"
            val wordGu = if (i <= 10) gujaratiDigits[i] else "નંબર $i"
            list.add(NumberItem(i, wordEn, wordHi, wordGu, emoji, color))
        }
        return list
    }

    val colorsList = listOf(
        ColorItem("Red", "लाल", "લાલ", 0xFFFF1744, "Red Apple", "लाल सेब", "લાલ સફરજન", "🍎"),
        ColorItem("Blue", "नीला", "વાદળી", 0xFF2979FF, "Blue Sky", "नीला आसमान", "વાદળી આકાશ", "🌌"),
        ColorItem("Yellow", "पीला", "પીળો", 0xFFFFEA00, "Yellow Sun", "पीला सूरज", "પીળો સૂર્ય", "☀️"),
        ColorItem("Green", "हरा", "લીલો", 0xFF00E676, "Green Leaf", "हरा पत्ता", "લીલું પાંદડું", "🍃"),
        ColorItem("Orange", "नारंगी", "નારંગી", 0xFFFF9100, "Orange Orange", "संतरा", "નારંગી", "🍊"),
        ColorItem("Pink", "गुलाबी", "ગુલાબી", 0xFFFF4081, "Pink Flower", "गुलाबी फूल", "ગુલાબી ફૂલ", "🌸"),
        ColorItem("Purple", "बैंगनी", "જાંબલી", 0xFFAA00FF, "Purple Grapes", "बैंगनी अंगूर", "જાંબલી દ્રાક્ષ", "🍇"),
        ColorItem("Cyan", "आसमानी", "હલકો વાદળી", 0xFF00E5FF, "Cyan Water", "नीला पानी", "વાદળી પાણી", "💧"),
        ColorItem("Brown", "भूरा", "બદામી", 0xFF8D6E63, "Brown Teddy Bear", "भूरा भालू", "બદામી ટેડી", "🧸"),
        ColorItem("White", "सफेद", "સફેદ", 0xFFFFFFFF, "White Milk", "सफेद दूध", "સફેદ દૂધ", "🥛"),
        ColorItem("Black", "काला", "કાળો", 0xFF212121, "Black Cat", "काली बिल्ली", "કાળી બિલાડી", "🐈‍⬛"),
        ColorItem("Gold", "सुनहरा", "સોનેરી", 0xFFFFD700, "Gold Crown", "सोने का मुकुट", "સોનાનો મુગટ", "👑")
    )

    val shapesList = listOf(
        ShapeItem("Circle", "वृत्त (गोल)", "ગોળ", "⭕", 0, "Coin / Clock", "सिक्का / घड़ी", "સિક્કો / ઘડિયાળ"),
        ShapeItem("Square", "वर्ग", "ચોરસ", "🔲", 4, "Window / Chess Board", "खिड़की", "બારી"),
        ShapeItem("Triangle", "त्रिभुज", "ત્રિકોણ", "🔺", 3, "Pizza Slice / Pyramid", "पिज्जा स्लाइस", "પિઝા સ્લાઈસ"),
        ShapeItem("Star", "तारा", "તારો", "⭐️", 5, "Night Sky Star", "आसमान का तारा", "આકાશનો તારો"),
        ShapeItem("Heart", "दिल", "હૃદય", "❤️", 0, "Love Symbol / Balloon", "गुब्बारा", "ફુગ્ગો"),
        ShapeItem("Rectangle", "आयत", "લંબચોરસ", "▭", 4, "Door / Smartphone", "दरवाजा", "દરવાજો"),
        ShapeItem("Oval", "अंडाकार", "ઈંડાકાર", "🥚", 0, "Egg / Mirror", "अंडा", "ઈંડું"),
        ShapeItem("Diamond", "हीरा", "હીરો", "🔷", 4, "Kite / Playing Card", "पतंग", "પતંગ"),
        ShapeItem("Pentagon", "पंचभुज", "પંચકોણ", "⬟", 5, "Schoolhouse Sign", "स्कूल का चिन्ह", "શાળાનું ચિહ્ન"),
        ShapeItem("Hexagon", "षट्भुज", "ષટ્કોણ", "⬡", 6, "Bee Honeycomb", "मधुमक्खी का छत्ता", "મધપૂડો")
    )

    val animalsList = listOf(
        AnimalItem("Lion", "शेर", "સિંહ", "🦁", "Roar! Roar!", "Wild", "Lions are kings of the savanna!", "शेर जंगल का राजा है!", "સિંહ જંગલનો રાજા છે!"),
        AnimalItem("Elephant", "हाथी", "હાથી", "🐘", "Trumpet! Pawoo!", "Wild", "Elephants have huge ears and tusks!", "हाथी सबसे बड़ा ज़मीनी जानवर है!", "હાથી સૌથી મોટું પ્રાણી છે!"),
        AnimalItem("Monkey", "बंदर", "વાંદરો", "🐒", "Chatter! Ooh ooh ah ah!", "Wild", "Monkeys love jumping on trees!", "बंदर पेड़ पर उछलते हैं!", "વાંદરા ઝાડ પર કૂદે છે!"),
        AnimalItem("Dog", "कुत्ता", "કૂતરો", "🐶", "Woof! Woof!", "Pet", "Dogs love playing fetch!", "कुत्ता वफादार होता है!", "કૂતરો વફાદાર પ્રાણી છે!"),
        AnimalItem("Cat", "बिल्ली", "બિલાડી", "🐱", "Meow! Purr...", "Pet", "Cats love drinking fresh milk!", "बिल्ली म्याऊं करती है!", "બિલાડી દૂધ પીવે છે!"),
        AnimalItem("Tiger", "बाघ", "વાઘ", "🐯", "Grrr! Roar!", "Wild", "Tigers have orange black stripes!", "बाघ हमारा राष्ट्रीय पशु है!", "વાઘ ભારતનું રાષ્ટ્રીય પ્રાણી છે!"),
        AnimalItem("Cow", "गाय", "ગાય", "🐮", "Moo! Moo!", "Farm", "Cows give nutritious healthy milk!", "गाय हमें दूध देती है!", "ગાય દૂધ આપે છે!"),
        AnimalItem("Panda", "पांडा", "પાંડા", "🐼", "Munch! Munch!", "Wild", "Pandas eat green bamboo leaves!", "पांडा बांस खाता है!", "પાંડા વાંસ ખાય છે!"),
        AnimalItem("Rabbit", "खरगोश", "સસલું", "🐰", "Squeak! Squeak!", "Pet", "Rabbits love eating crunchy carrots!", "खरगोश गाजर खाता है!", "સસલું ગાજર ખાય છે!"),
        AnimalItem("Bear", "भालू", "રીંછ", "🐻", "Growl! Grrr!", "Wild", "Bears love sweet honey!", "भालू को शहद बहुत पसंद है!", "રીંછને મધ ખૂબ ભાવે છે!"),
        AnimalItem("Dolphin", "डॉल्फ़िन", "ડોલ્ફિન", "🐬", "Click! Click!", "Ocean", "Dolphins are smart ocean friends!", "डॉल्फ़िन बहुत समझदार होती है!", "ડોલ્ફિન બુદ્ધિશાળી પ્રાણી છે!")
    )

    val birdsList = listOf(
        BirdItem("Peacock", "मोर", "મોર", "🦚", "Meow-k! Meow-k!"),
        BirdItem("Parrot", "तोता", "પોપટ", "🦜", "Squawk! Mithu Mithu!"),
        BirdItem("Owl", "उल्लू", "ઘુવડ", "🦉", "Hoot! Hoot!"),
        BirdItem("Duck", "बतख", "બતક", "🦆", "Quack! Quack!"),
        BirdItem("Penguin", "पेन्गुइन", "પેન્ગ્વિન", "🐧", "Honk! Honk!"),
        BirdItem("Swan", "हंस", "હંસ", "🦢", "Coos... Coos..."),
        BirdItem("Flamingo", "हंस पक्षी", "ફલેમિંગો", "🦩", "Chirp! Chirp!"),
        BirdItem("Eagle", "चील / बाज", "ગરુડ", "🦅", "Screech!")
    )

    val fruitsVegList = listOf(
        FruitVegItem("Apple", "सेब", "સફરજન", "🍎", true, "Red"),
        FruitVegItem("Banana", "केला", "કેળું", "🍌", true, "Yellow"),
        FruitVegItem("Mango", "आम", "કેરી", "🥭", true, "Yellow"),
        FruitVegItem("Strawberry", "स्ट्रॉबेरी", "સ્ટ્રોબેરી", "🍓", true, "Red"),
        FruitVegItem("Watermelon", "तरबूज", "તરબૂચ", "🍉", true, "Green"),
        FruitVegItem("Carrot", "गाजर", "ગાજર", "🥕", false, "Orange"),
        FruitVegItem("Broccoli", "ब्रोकोली", "બ્રોકોલી", "🥦", false, "Green"),
        FruitVegItem("Tomato", "टमाटर", "ટમેટું", "🍅", false, "Red"),
        FruitVegItem("Corn", "मक्का", "મકાઈ", "🌽", false, "Yellow"),
        FruitVegItem("Potato", "आलू", "બટાટા", "🥔", false, "Brown")
    )

    val vehiclesList = listOf(
        VehicleItem("Car", "कार", "કાર", "🚗", "Vroom Vroom!", "Land"),
        VehicleItem("Bus", "बस", "બસ", "🚌", "Honk Honk!", "Land"),
        VehicleItem("School Bus", "स्कूल बस", "સ્કૂલ બસ", "🟡", "Beep Beep!", "Land"),
        VehicleItem("Train", "ट्रेन", "ટ્રેન", "🚂", "Choo Choo! Chug chug!", "Land"),
        VehicleItem("Airplane", "हवाई जहाज", "વિમાન", "✈️", "Whoosh! Zoom!", "Air"),
        VehicleItem("Helicopter", "हेलीकॉप्टर", "હેલિકોપ્ટર", "🚁", "Thump thump thump!", "Air"),
        VehicleItem("Fire Truck", "दमकल गाड़ी", "ફાયર બ્રિગેડ", "🚒", "Neno Neno Wee-woo!", "Land"),
        VehicleItem("Rocket", "रॉकेट", "રોકેટ", "🚀", "3 2 1 Blast Off!", "Air"),
        VehicleItem("Ship", "पानी का जहाज", "જહાજ", "🚢", "Hoooot! Hoooot!", "Water")
    )

    val rhymesList = listOf(
        RhymeItem(
            id = "en_twinkle",
            titleEn = "Twinkle Twinkle Little Star",
            titleHi = "ट्विंकल ट्विंकल लिटिल स्टार",
            titleGu = "ટ્વિંકલ ટ્વિંકલ લિટલ સ્ટાર",
            language = "en",
            lyrics = listOf(
                "Twinkle, twinkle, little star,",
                "How I wonder what you are!",
                "Up above the world so high,",
                "Like a diamond in the sky.",
                "Twinkle, twinkle, little star,",
                "How I wonder what you are!"
            ),
            icon = "⭐️",
            bgGradient = longArrayOf(0xFF1E1B4B, 0xFF4338CA)
        ),
        RhymeItem(
            id = "en_johny",
            titleEn = "Johny Johny Yes Papa",
            titleHi = "जॉनी जॉनी यस पापा",
            titleGu = "જોની જોની યસ પપ્પા",
            language = "en",
            lyrics = listOf(
                "Johny, Johny, Yes Papa?",
                "Eating sugar? No Papa!",
                "Telling lies? No Papa!",
                "Open your mouth, Ha! Ha! Ha!"
            ),
            icon = "👶",
            bgGradient = longArrayOf(0xFF065F46, 0xFF059669)
        ),
        RhymeItem(
            id = "hi_chanda",
            titleEn = "Chanda Mama Door Ke",
            titleHi = "चंदा मामा दूर के",
            titleGu = "ચંદા મામા દૂર કે",
            language = "hi",
            lyrics = listOf(
                "चंदा मामा दूर के,",
                "पुए पकाएं बूर के!",
                "आप खाएं थाली में,",
                "मुन्ने को दें प्याली में!",
                "प्याली गई टूट,",
                "मुन्ना गया रूठ!",
                "लाएंगे नई प्यालियां,",
                "बजा बजा के तालियां!"
            ),
            icon = "🌙",
            bgGradient = longArrayOf(0xFF881337, 0xFFE11D48)
        ),
        RhymeItem(
            id = "hi_machhli",
            titleEn = "Machhli Jal Ki Rani Hai",
            titleHi = "मछली जल की रानी है",
            titleGu = "મછલી જલ કી રાની હૈ",
            language = "hi",
            lyrics = listOf(
                "मछली जल की रानी है,",
                "जीवन उसका पानी है!",
                "हाथ लगाओ तो डर जाएगी,",
                "बाहर निकालो तो मर जाएगी!"
            ),
            icon = "🐟",
            bgGradient = longArrayOf(0xFF0C4A6E, 0xFF0284C7)
        ),
        RhymeItem(
            id = "gu_chaki",
            titleEn = "Chaki Chaki Re Chaki",
            titleHi = "चाकी चाकी रे चाकी",
            titleGu = "ચકી ચકી રે ચકી",
            language = "gu",
            lyrics = listOf(
                "ચકી ચકી રે ચકી,",
                "મારી સાથે રમવા આવિશ કે નહિ?",
                "બેસવાને ખાટલો,",
                "સુવાને પાટલો,",
                "પહેરવાને સાડી,",
                "મોરપીંછ વાળી!"
            ),
            icon = "🐦",
            bgGradient = longArrayOf(0xFF701A75, 0xFFC026D3)
        ),
        RhymeItem(
            id = "gu_mama",
            titleEn = "Mama Nu Ghar Ketle",
            titleHi = "मामा नु घर केटले",
            titleGu = "મામા નું ઘર કેટલે",
            language = "gu",
            lyrics = listOf(
                "મામા નું ઘર કેટલે?",
                "દીવો બળે એટલે!",
                "દીવો તો મેં જોયો,",
                "મામો મારો સોયો!"
            ),
            icon = "🏡",
            bgGradient = longArrayOf(0xFF78350F, 0xFFD97706)
        )
    )

    val quizQuestions = listOf(
        QuizQuestion(
            id = 1,
            category = "ABC",
            questionEn = "What letter comes after 'A'?",
            questionHi = "'A' के बाद कौन सा अक्षर आता है?",
            questionGu = "'A' પછી કયો અક્ષર આવે છે?",
            options = listOf("B", "C", "D", "E"),
            correctIndex = 0,
            explanationEn = "B comes right after A in the alphabet!"
        ),
        QuizQuestion(
            id = 2,
            category = "ABC",
            questionEn = "Which animal starts with the letter 'L'?",
            questionHi = "'L' अक्षर से कौन सा जानवर शुरू होता है?",
            questionGu = "'L' અક્ષરથી કયું પ્રાણી શરૂ થાય છે?",
            options = listOf("Cat", "Dog", "Lion", "Pig"),
            correctIndex = 2,
            explanationEn = "Lion starts with L! L for Lion!"
        ),
        QuizQuestion(
            id = 3,
            category = "Numbers",
            questionEn = "How many fingers do you have on one hand?",
            questionHi = "एक हाथ में कितनी उंगलियां होती हैं?",
            questionGu = "એક હાથમાં કેટલી આંગળીઓ હોય છે?",
            options = listOf("3", "4", "5", "10"),
            correctIndex = 2,
            explanationEn = "Count 1, 2, 3, 4, 5 fingers!"
        ),
        QuizQuestion(
            id = 4,
            category = "Animals",
            questionEn = "Which animal is the King of the Jungle?",
            questionHi = "जंगल का राजा कौन सा जानवर है?",
            questionGu = "જંગલનો રાજા કયું પ્રાણી છે?",
            options = listOf("Tiger", "Elephant", "Lion", "Monkey"),
            correctIndex = 2,
            explanationEn = "The mighty Lion is the King of the Jungle!"
        ),
        QuizQuestion(
            id = 5,
            category = "Colors",
            questionEn = "What color is an Apple?",
            questionHi = "सेब का रंग कैसा होता है?",
            questionGu = "સફરજનનો રંગ કેવો હોય છે?",
            options = listOf("Blue", "Red", "Black", "Purple"),
            correctIndex = 1,
            explanationEn = "Ripe apples are bright RED!"
        )
    )

    val coloringPages = listOf(
        ColoringPageTemplate("c1", "Friendly Lion", "Animals", "🦁", "M 20 50 Q 50 10 80 50 Q 90 80 50 90 Q 10 80 20 50 Z"),
        ColoringPageTemplate("c2", "Cute Teddy Bear", "Cartoons", "🧸", "M 30 30 Circle 30 30 20"),
        ColoringPageTemplate("c3", "Fast Racing Car", "Vehicles", "🏎️", "M 10 60 H 90 V 80 H 10 Z"),
        ColoringPageTemplate("c4", "Yummy Strawberry", "Fruits", "🍓", "M 20 20 Q 50 90 80 20 Z"),
        ColoringPageTemplate("c5", "Magic Star & Moon", "Space", "🌙", "M 50 20 Star")
    )

    val puzzleData = listOf(
        JigsawPuzzleData("p1", "Lion King", "🦁", "Animals", 0xFFFF9100),
        JigsawPuzzleData("p2", "Cute Elephant", "🐘", "Animals", 0xFF00B0FF),
        JigsawPuzzleData("p3", "Red Car", "🚗", "Vehicles", 0xFFFF5252),
        JigsawPuzzleData("p4", "Yummy Apple", "🍎", "Fruits", 0xFF00E676),
        JigsawPuzzleData("p5", "Bright Sun", "☀️", "Space", 0xFFFFD600)
    )
}
