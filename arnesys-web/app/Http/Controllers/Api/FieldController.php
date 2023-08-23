<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Field;

class FieldController extends Controller
{

    public function show($id)
    {
        $fields = Field::where('client_id', $id)->paginate(5);
        return $fields;
    }

}
