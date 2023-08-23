<?php

use App\Http\Controllers\Auth\LoginController;
use App\Http\Controllers\ClientController;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\FrontEndController;
use App\Http\Controllers\FieldController;
use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\Auth;

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "web" middleware group. Make something great!
|
*/

Auth::routes([
    'register' => false,
    'reset' => false,
    'verify' => false,
]);

Route::get('/', [FrontEndController::class, 'index'])->name('index');

// Handle Login & Register
Route::get('/auth/login', [LoginController::class, 'index'])->name('auth.login');
Route::post('/auth/login/post', [LoginController::class, 'handleLogin'])->name('auth.login.post');

Route::group(['prefix' => 'master', 'middleware' => ['auth:web,webstudent,webclient', 'verified']], function () {

    Route::get('/dashboard', [DashboardController::class, 'index'])->name('dashboard');
    Route::get('/fields/detail/main-device/{id}', [FieldController::class, 'detailMainDevice'])->name('client.field.detail.main');
    Route::get('/fields/detail/support-device/{id}/{number}', [FieldController::class, 'detailSupportDevice'])->name('client.field.detail.support');

    // Role Operator
    Route::group(['middleware' => ['role:Operator']], function () {
        Route::get('/clients', [ClientController::class, 'index'])->name('client');
        Route::get('/client/create', [ClientController::class, 'create'])->name('client.create');
        Route::get('/fields/{id}', [FieldController::class, 'getByClient'])->name('client.field');
        Route::delete('/client/delete/{id}', [ClientController::class, 'delete'])->name('client.delete');
        Route::post('/client/post', [ClientController::class, 'store'])->name('client.store');
        Route::delete('/fields/delete/{id}', [FieldController::class, 'delete'])->name('field.delete');
        Route::post('/field/store', [FieldController::class, 'store'])->name('field.store');
    });

    // Role Client
    Route::group(['middleware' => ['role:Client']], function () {
        Route::get('/fields', [FieldController::class, 'index'])->name('field');
    });

});
