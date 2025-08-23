package com.example.icecreampos.di

import com.example.icecreampos.data.repository.*
import com.example.icecreampos.ui.viewmodel.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    single { AuthRepository(get()) }
    single { UserRepository(get()) }
    single { CategoryRepository(get()) }
    single { ProductRepository(get(), get()) }
    single { ToppingRepository(get()) }
    single { OrderRepository(get()) }
    single { SettingsRepository(get(), get()) }

    viewModel { LoginViewModel(get(), get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { ProductViewModel(get(), get()) }
    viewModel { ToppingViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { CartViewModel() }
    viewModel { PaymentViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
