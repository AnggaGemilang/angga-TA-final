<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Ramsey\Uuid\Uuid as RamseyUuid;

class MonitoringSupportDevice extends Model
{
    use HasFactory;

    protected $fillable = [
        'number_of',
        'soil_temperature',
        'soil_humidity',
        'soil_ph',
        'soil_nitrogen',
        'soil_phosphor',
        'soil_kalium',
        'field_id'
    ];

    public static function boot()
    {
        parent::boot();
        static::creating(function($obj) {
            $obj->id = RamseyUuid::uuid4()->toString();
        });
    }

}
