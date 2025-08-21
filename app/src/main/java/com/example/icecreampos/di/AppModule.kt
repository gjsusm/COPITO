package com.example.icecreampos.di

import com.example.icecreampos.data.SessionManager
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

    single { AuthRepository(get<FirebaseAuth>()) }
    single { UserRepository(get<FirebaseFirestore>()) }
    single { CategoryRepository(get<FirebaseFirestore>()) }
    single { ProductRepository(get<FirebaseFirestore>(), get<FirebaseStorage>()) }
    single { ToppingRepository(get<FirebaseFirestore>()) }
    single { OrderRepository(get<FirebaseFirestore>()) }
    single { CustomerRepository(get<FirebaseFirestore>()) }
    single { SettingsRepository(get<FirebaseFirestore>(), get<FirebaseStorage>()) }
    single { SessionManager() }


    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { UserViewModel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { ProductViewModel(get(), get()) }
    viewModel { ToppingViewModel(get()) }
    viewModel { CartViewModel() }
    viewModel { PaymentViewModel(get(), get()) }
    viewModel { CustomerDNIViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { UserRoleViewModel() }
}
