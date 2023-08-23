<?php

namespace Database\Seeders;

use App\Models\Field;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;

class FieldSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        Field::create([
            'address' => 'Bandung',
            'plant_type' => 'Tomato',
            'land_area' => '120 ha',
            'number_of_support_device' => 2,
            'client_id' => '123123123',
            'created_at' => date('Y-m-d H:i:s'),
            'updated_at' => date('Y-m-d H:i:s'),
        ]);

        Field::create([
            'address' => 'Tasikmalaya',
            'plant_type' => 'Spinach',
            'land_area' => '120 ha',
            'number_of_support_device' => 1,
            'client_id' => '123123123',
            'created_at' => date('Y-m-d H:i:s'),
            'updated_at' => date('Y-m-d H:i:s'),
        ]);

    }
}
