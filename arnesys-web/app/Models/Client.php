<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Ramsey\Uuid\Uuid as RamseyUuid;
use Spatie\Permission\Traits\HasRoles;

class Client extends Authenticatable
{
    use HasFactory;
    use HasRoles;

    protected $keyType = 'string';

    protected $fillable = [
        'name',
        'email',
        'password',
        'photo',
    ];

    protected $guarded = [];

    protected $guard_name = 'webclient';

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
