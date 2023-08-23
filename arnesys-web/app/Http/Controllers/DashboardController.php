<?php

namespace App\Http\Controllers;

use App\Models\Client;
use App\Models\Field;
use Illuminate\Support\Facades\Auth;
use Illuminate\Http\Request;

class DashboardController extends Controller
{

    function index(){
        // Admin

        $numberOfFields = Field::all()->count();
        $numberOfClients = Client::all()->count();

        // Client

        $numberOfFieldsByClient = Field::where('client_id', Auth::user()->id)->get()->count();

        return view('master.dashboard', compact('numberOfFields', 'numberOfClients', 'numberOfFieldsByClient'));
    }

}
