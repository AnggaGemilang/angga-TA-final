<?php

namespace Database\Seeders;

use App\Models\Client;
use App\Models\User;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;

class ClientsSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $ClientRole = Client::create([
            'first_name' => 'Angga',
            'last_name' => 'Gemilang',
            'username' => 'angga_gemilang',
            'no_telp' => '083195008217',
            'address' => 'Bandung',
            'email' => 'angga@polban.ac.id',
            'email_verified_at' => now(),
            'password' => Hash::make('angga123'),
            'remember_token' => Str::random(10),
            'created_at' => date('Y-m-d H:i:s'),
            'updated_at' => date('Y-m-d H:i:s'),
        ]);

        $ClientRole->assignRole('Client');
    }
}
