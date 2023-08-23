<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Ramsey\Uuid\Uuid as RamseyUuid;

class Field extends Model
{
    use HasFactory;

    protected $keyType = 'string';

    protected $fillable = [
        'address',
        'plant_type',
        'thumbnail',
        'number_of_support_device',
        'client_id',
        'meta_data'
    ];

    protected $casts = [
        'id' => 'string',
    ];

    public $incrementing = false;

    public static function boot()
    {
        parent::boot();
        static::creating(function($obj) {
            $obj->id = RamseyUuid::uuid4()->toString();
        });
    }

}
