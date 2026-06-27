# 🏆 Win or Learn — Esports Tournament Platform

> **"Every Match Makes You Better."**  
> *Made with ❤️ by Saurav*

---

## 📖 Introduction
**Win or Learn** is a modern, premium, full-scale Esports Tournament Platform built for competitive mobile gaming. While designed to scale to multiple titles in the future (including BGMI, COD Mobile, Valorant, eFootball, and Chess), the platform initially offers fully functional brackets and match environments for **Free Fire**.

This project contains a **production-ready Android Application** designed with **Kotlin**, **Jetpack Compose**, and **Room Database**. It delivers a high-fidelity dark-themed gaming aesthetic with electric blue, purple, and gold accents.

---

## 🛠️ Features Implemented
1. **Seamless Splash & Onboarding**: Fully styled animations illustrating key features (clans, tournaments, instant cashouts) with a custom gaming logo.
2. **Interactive Credentials Flow**: Securely styled sign-up and sign-in processes featuring an instant Demo Bypass specifically configured for the profile of Saurav.
3. **Dynamic Arena Dashboard**: Interactive tabs separating *Upcoming*, *Live*, and *Completed* tournament brackets. Features status-specific badges and custom progress bars tracking slot bookings.
4. **Real-Time Room Credentials & Timer**: Secure, automated access to custom lobby Room IDs and passwords. Real-time ticker countdowns notify users exactly when the match begins.
5. **Tactical Clan Chat**: Fully active local database clan community link. Players can found clans, join recruiting teams, and converse in a real-time chat bubble system.
6. **Esports Wallet & Cashouts**: Track cash balances, credit reward coins, simulate gateway deposits, request bank withdrawals, and inspect full transaction history ledgers.
7. **Performance Analytics (Canvas Line Chart)**: High-performance, lightweight vector line charts drawn directly via Jetpack Compose Canvas tracking historic kills trends.
8. **Power-Packed Admin Panel**: Dedicated administrative command center to publish new brackets, edit details, release Room IDs instantly, and declare match results (disbursing prize pools dynamically).

---

## 🗄️ Supabase PostgreSQL Database DDL
Below is the complete database structure requested for Supabase (PostgreSQL). Run this script in the **Supabase SQL Editor** to establish the tables, foreign keys, row-level security (RLS) policies, and performance indexes.

```sql
-- 1. Create Profiles Table (Linked to Supabase Auth.users)
CREATE TABLE public.profiles (
    id UUID REFERENCES auth.users(id) ON DELETE CASCADE PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    game_uid VARCHAR(30) NOT NULL,
    bio TEXT,
    rank_tier VARCHAR(30) DEFAULT 'Diamond III',
    level INT DEFAULT 1,
    xp INT DEFAULT 0,
    xp_max INT DEFAULT 1000,
    balance_cash DECIMAL(10, 2) DEFAULT 50.00,
    balance_coins INT DEFAULT 100,
    clan_id BIGINT,
    referral_code VARCHAR(20) UNIQUE NOT NULL,
    total_matches INT DEFAULT 0,
    total_kills INT DEFAULT 0,
    wins INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 2. Create Clans Table
CREATE TABLE public.clans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    tag VARCHAR(4) UNIQUE NOT NULL,
    points INT DEFAULT 100,
    logo_seed INT DEFAULT 1,
    leader_username VARCHAR(50) NOT NULL,
    member_count INT DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- Link profiles clan_id foreign key
ALTER TABLE public.profiles 
ADD CONSTRAINT fk_clan FOREIGN KEY (clan_id) REFERENCES public.clans(id) ON DELETE SET NULL;

-- 3. Create Tournaments Table
CREATE TABLE public.tournaments (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    game VARCHAR(50) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('UPCOMING', 'LIVE', 'COMPLETED')) DEFAULT 'UPCOMING',
    entry_type VARCHAR(20) CHECK (entry_type IN ('FREE', 'PAID', 'COINS')) DEFAULT 'FREE',
    entry_fee DECIMAL(10, 2) DEFAULT 0.00,
    entry_coins INT DEFAULT 0,
    prize_pool DECIMAL(10, 2) NOT NULL,
    max_slots INT NOT NULL,
    registered_slots INT DEFAULT 0,
    match_time BIGINT NOT NULL,
    game_type VARCHAR(10) CHECK (game_type IN ('Solo', 'Duo', 'Squad')) DEFAULT 'Solo',
    rules TEXT,
    map_name VARCHAR(50) NOT NULL,
    room_id VARCHAR(50) DEFAULT '',
    room_password VARCHAR(50) DEFAULT '',
    winner_username VARCHAR(50),
    winner_kills INT,
    mvp_username VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 4. Create Registrations Table (Composite Key)
CREATE TABLE public.registrations (
    tournament_id BIGINT REFERENCES public.tournaments(id) ON DELETE CASCADE,
    username VARCHAR(50) NOT NULL,
    game_uid VARCHAR(30) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('PENDING', 'APPROVED')) DEFAULT 'APPROVED',
    PRIMARY KEY (tournament_id, username)
);

-- 5. Create Transactions Table
CREATE TABLE public.transactions (
    id BIGSERIAL PRIMARY KEY,
    profile_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    amount DECIMAL(10, 2) NOT NULL,
    type VARCHAR(30) CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'ENTRY_FEE', 'PRIZE_WINNING', 'REFERRAL_BONUS')),
    status VARCHAR(20) CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')) DEFAULT 'PENDING',
    timestamp BIGINT NOT NULL,
    description TEXT NOT NULL
);

-- 6. Create Clan Messages Table
CREATE TABLE public.clan_messages (
    id BIGSERIAL PRIMARY KEY,
    clan_id BIGINT REFERENCES public.clans(id) ON DELETE CASCADE,
    sender VARCHAR(50) NOT NULL,
    text TEXT NOT NULL,
    timestamp BIGINT NOT NULL
);

-- 7. Add Performance Indexes
CREATE INDEX idx_tournaments_status ON public.tournaments(status);
CREATE INDEX idx_registrations_username ON public.registrations(username);
CREATE INDEX idx_transactions_profile ON public.transactions(profile_id);
CREATE INDEX idx_clan_messages_clan ON public.clan_messages(clan_id);

-- 8. Enable Row Level Security (RLS)
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.clan_messages ENABLE ROW LEVEL SECURITY;

-- 9. Setup Basic Policies
CREATE POLICY "Users can view their own profile data" ON public.profiles
    FOR SELECT USING (auth.uid() = id);

CREATE POLICY "Users can update their own profile data" ON public.profiles
    FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users can view their own transaction history" ON public.transactions
    FOR SELECT USING (auth.uid() = profile_id);

CREATE POLICY "Clan members can view clan chat messages" ON public.clan_messages
    FOR SELECT USING (
        EXISTS (
            SELECT 1 FROM public.profiles 
            WHERE profiles.id = auth.uid() AND profiles.clan_id = clan_messages.clan_id
        )
    );
```

---

## 💻 Installation & Compilation Guide

### Android Studio (Local Compilation)
To compile and test this project on your physical device or emulator using Android Studio:
1. **Clone/Download**: Extract the project zip repository files.
2. **Open Project**: Launch Android Studio, click **File > Open**, select the root directory containing `settings.gradle.kts`.
3. **Sync Gradle**: Allow Android Studio to download dependencies (Navigation, Coil, Room, KSP symbol processor).
4. **Run Application**: Press **Shift + F10** (or the green Play button) to install and launch **Win or Learn** on your connected device.

### In AI Studio Preview
The application builds incrementally and automatically runs in the **Streaming Emulator** on the right sidebar. 

---

## 🚀 Deployment Guide
1. **Generate Signable APK**:
   - In Android Studio, go to **Build > Generate Signed Bundle / APK**.
   - Create a keystore and set secure build passwords.
   - Select **V2 (Full APK Signature)** and export the production `.apk` or `.aab` file.
2. **Publish to Play Store**:
   - Register on the Google Play Console.
   - Setup store listings, screenshots, and age ratings.
   - Upload the `.aab` (Android App Bundle) file to the Production track.
