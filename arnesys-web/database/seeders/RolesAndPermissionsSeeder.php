<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Spatie\Permission\Models\Role;
use Spatie\Permission\PermissionRegistrar;

class RolesAndPermissionsSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        app()[PermissionRegistrar::class]->forgetCachedPermissions();

        $OperatorRole = Role::create(
            [
            'name' => 'Operator',
            'guard_name' => 'web',
            ]
        );

        $ClientRole = Role::create([
            'name' => 'Client',
            'guard_name' => 'webclient',
        ]);
    }
}
