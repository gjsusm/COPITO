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


    viewModel { LoginViewModel(get<AuthRepository>(), get<UserRepository>(), get<SessionManager>()) }
    viewModel { UserViewModel(get<UserRepository>()) }
    viewModel { CategoryViewModel(get<CategoryRepository>()) }
    viewModel { ProductViewModel(get<ProductRepository>(), get<FirebaseStorage>()) }
    viewModel { ToppingViewModel(get<ToppingRepository>()) }
    viewModel { CartViewModel() }
    viewModel { PaymentViewModel(get<OrderRepository>(), get<CustomerRepository>()) }
    viewModel { CustomerDNIViewModel(get<CustomerRepository>()) }
    viewModel { SettingsViewModel(get<SettingsRepository>()) }
    viewModel { UserRoleViewModel() }
}
