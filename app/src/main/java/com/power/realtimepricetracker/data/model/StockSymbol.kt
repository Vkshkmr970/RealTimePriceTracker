package com.power.realtimepricetracker.data.model

data class StockSymbols(
    val ticker: String,
    val name: String,
    val description: String
){
    companion object {
        val ALL = listOf(
            StockSymbols(
                "AAPL", "Apple Inc.",
                "Apple designs and sells consumer electronics, software, and online services. " +
                        "The iPhone is the company's primary revenue driver, complemented by the Mac, iPad, " +
                        "Apple Watch, and AirPods. Its services segment—including the App Store, iCloud, " +
                        "Apple TV+, and Apple Pay—has become a significant and fast-growing part of the business."
            ),
            StockSymbols(
                "MSFT", "Microsoft Corporation",
                "Microsoft is one of the world's most valuable companies, with a broad portfolio " +
                        "spanning cloud infrastructure (Azure), productivity software (Microsoft 365), gaming " +
                        "(Xbox), and enterprise tools. Its acquisition of LinkedIn and GitHub has strengthened " +
                        "its position in professional networking and developer tooling."
            ),
            StockSymbols(
                "NVDA", "NVIDIA Corporation",
                "NVIDIA started as a GPU company for gaming but has evolved into a dominant force in " +
                        "AI computing. Its data-center chips power the large language models and AI workloads " +
                        "driving the current tech revolution. The CUDA programming platform has created a deep " +
                        "moat around its hardware ecosystem."
            ),
            StockSymbols(
                "GOOG", "Alphabet Inc.",
                "Alphabet is the parent company of Google, the world's dominant search engine and " +
                        "digital advertising platform. Google Cloud is growing rapidly as an enterprise " +
                        "infrastructure provider, while YouTube remains the largest video-sharing platform " +
                        "globally. Alphabet also invests in moonshot projects through its Other Bets division."
            ),
            StockSymbols(
                "AMZN", "Amazon.com Inc.",
                "Amazon operates the world's largest e-commerce marketplace alongside AWS, the leading " +
                        "cloud infrastructure platform. AWS contributes the majority of Amazon's operating profit " +
                        "despite being a smaller portion of revenue. Amazon also owns Whole Foods, Prime Video, " +
                        "Twitch, and Ring."
            ),
            StockSymbols(
                "META", "Meta Platforms Inc.",
                "Meta owns Facebook, Instagram, and WhatsApp—three of the world's most widely used " +
                        "social platforms. Its advertising business generates tens of billions in annual revenue. " +
                        "Meta is investing heavily in its Reality Labs division to build the metaverse, " +
                        "though these efforts have yet to turn a profit."
            ),
            StockSymbols(
                "TSLA", "Tesla Inc.",
                "Tesla is the leading manufacturer of electric vehicles and a major player in clean " +
                        "energy storage and solar solutions. The company operates Gigafactories across the US, " +
                        "Europe, and China. Its Full Self-Driving software suite represents Tesla's long-term " +
                        "bet on autonomous transportation."
            ),
            StockSymbols(
                "NFLX", "Netflix Inc.",
                "Netflix pioneered subscription-based streaming entertainment and remains the largest " +
                        "global streaming platform by subscriber count. The company produces award-winning " +
                        "original content across drama, comedy, documentary, and animation. Its ad-supported " +
                        "tier has opened new revenue streams following years of subscription-only growth."
            ),
            StockSymbols(
                "ADBE", "Adobe Inc.",
                "Adobe dominates the creative software market with Photoshop, Illustrator, Premiere Pro, " +
                        "and the broader Creative Cloud suite. Its shift to subscription-based licensing has " +
                        "created highly predictable recurring revenue. Adobe's Document Cloud and Experience " +
                        "Cloud platforms extend its reach into enterprise workflows."
            ),
            StockSymbols(
                "CRM", "Salesforce Inc.",
                "Salesforce is the global leader in cloud-based customer relationship management software. " +
                        "Its platform helps businesses manage sales, marketing, customer service, and analytics. " +
                        "The acquisition of Slack added a collaboration layer to its enterprise offering, " +
                        "competing directly with Microsoft Teams."
            ),
            StockSymbols(
                "AMD", "Advanced Micro Devices",
                "AMD designs high-performance CPUs and GPUs for gaming, workstations, and data centers. " +
                        "Its Ryzen and EPYC processor lines have taken significant market share from Intel, " +
                        "while its Instinct GPUs are emerging as an alternative to NVIDIA in AI training workloads."
            ),
            StockSymbols(
                "ORCL", "Oracle Corporation",
                "Oracle is a veteran enterprise technology company best known for its relational database " +
                        "software, which underpins mission-critical systems in banking, healthcare, and government. " +
                        "Oracle Cloud Infrastructure has been gaining enterprise cloud market share, and its " +
                        "acquisition of Cerner marked a major expansion into healthcare IT."
            ),
            StockSymbols(
                "SPOT", "Spotify Technology",
                "Spotify is the world's largest audio streaming platform, with a library of over " +
                        "100 million tracks and a fast-growing podcast segment. The company has been expanding " +
                        "into audiobooks and is investing in AI-powered discovery features to personalize " +
                        "listening experiences at scale."
            ),
            StockSymbols(
                "SHOP", "Shopify Inc.",
                "Shopify provides e-commerce infrastructure to over two million merchants globally, " +
                        "from independent sellers to major brands. Its platform handles storefront creation, " +
                        "payments, inventory, shipping, and marketing. Shopify's ecosystem of apps and partners " +
                        "has made it the default choice for direct-to-consumer brands."
            ),
            StockSymbols(
                "COIN", "Coinbase Global",
                "Coinbase is the largest regulated cryptocurrency exchange in the United States. " +
                        "It offers retail and institutional trading, custody, and staking services across " +
                        "hundreds of digital assets. Coinbase's revenue is closely tied to crypto market " +
                        "activity, making it highly sensitive to broader market cycles."
            ),
            StockSymbols(
                "UBER", "Uber Technologies",
                "Uber operates the world's largest ride-hailing platform alongside a major food delivery " +
                        "service, Uber Eats. The company has reached profitability after years of heavy losses, " +
                        "driven by growth in high-margin services. Uber Freight and autonomous vehicle " +
                        "partnerships point toward future diversification."
            ),
            StockSymbols(
                "PYPL", "PayPal Holdings",
                "PayPal is one of the world's leading digital payment platforms, operating PayPal, Venmo, " +
                        "and Braintree. It facilitates online and in-store payments for millions of merchants and " +
                        "consumers globally. The company has faced increasing competition from Apple Pay, " +
                        "Google Pay, and bank-led alternatives."
            ),
            StockSymbols(
                "INTC", "Intel Corporation",
                "Intel has long been the dominant manufacturer of x86 processors for personal computers " +
                        "and servers, though it has ceded ground to AMD and ARM-based alternatives. Its IDM 2.0 " +
                        "strategy aims to revive chip manufacturing leadership, with new fabs in the US and " +
                        "Europe backed by government subsidies."
            ),
            StockSymbols(
                "CSCO", "Cisco Systems",
                "Cisco is the global leader in enterprise networking hardware and security software, " +
                        "including routers, switches, firewalls, and collaboration tools. Its recurring software " +
                        "and subscription revenue has grown steadily, reducing dependence on hardware sales cycles. " +
                        "Cisco Webex competes in the enterprise video conferencing market."
            ),
            StockSymbols(
                "BABA", "Alibaba Group",
                "Alibaba is China's largest e-commerce and cloud computing company, operating Taobao, " +
                        "Tmall, and Alibaba Cloud. Regulatory pressure from the Chinese government significantly " +
                        "impacted its growth trajectory in recent years. The company has pursued a restructuring " +
                        "strategy to unlock value across its diverse business units."
            ),
            StockSymbols(
                "PLTR", "Palantir Technologies",
                "Palantir provides AI and data analytics platforms to government agencies and commercial " +
                        "enterprises. Its Gotham platform serves defense and intelligence clients, while Foundry " +
                        "and the AIP platform target enterprise data infrastructure. Palantir has been profitable " +
                        "on an adjusted basis since 2021."
            ),
            StockSymbols(
                "SQ", "Block Inc.",
                "Block (formerly Square) is a fintech company offering payment processing, point-of-sale " +
                        "hardware, and financial tools through its Square ecosystem for businesses. The Cash App " +
                        "serves consumers with peer-to-peer payments, investing, and Bitcoin services. Its vision " +
                        "centers on financial inclusion and decentralized financial systems."
            ),
            StockSymbols(
                "SNAP", "Snap Inc.",
                "Snap operates Snapchat, a camera and messaging app known for disappearing content, " +
                        "Stories, and augmented reality Lenses. The platform is particularly popular among " +
                        "younger demographics. Snap invests heavily in AR technology and its Spectacles hardware " +
                        "as long-term differentiators in the social media space."
            ),
            StockSymbols(
                "ZOOM", "Zoom Video Communications",
                "Zoom became a household name during the pandemic by offering easy-to-use video " +
                        "conferencing software. The company has since expanded into Zoom Phone, Zoom Contact " +
                        "Center, and AI-powered meeting features. Growth has moderated as hybrid-work patterns " +
                        "stabilized, but the platform remains widely used across enterprises."
            ),
            StockSymbols(
                "TWTR", "X Corp",
                "X Corp, formerly Twitter, is a global public conversation platform. Following its " +
                        "acquisition and rebranding, the company underwent a major transformation including " +
                        "significant operational changes and the introduction of premium subscriptions and " +
                        "creator monetization features. It remains one of the most influential real-time " +
                        "information networks in the world."
            )
        )

        fun find(ticker: String): StockSymbols? = ALL.find { it.ticker == ticker }
    }
}
