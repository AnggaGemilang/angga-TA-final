<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Client;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Illuminate\Support\Facades\Hash;

class AuthController extends Controller
{

    function login(Request $request){

        $client = Client::where('email', $request->email)->get();

        if(count($client) > 0){
            if (Hash::check($request->password, $client[0]->password)) {
                return response()->json([
                    'status' => 'success',
                    'message' => 'Login success',
                    'data' => $client[0],
                ], Response::HTTP_OK);
            } else {
                return response()->json([
                    'status' => 'failed',
                    'message' => 'Username or password is incorrect'
                ], Response::HTTP_NOT_FOUND);
            }
        }

        return response()->json([
            'status' => 'failed',
            'message' => 'Username or password is incorrect'
        ], Response::HTTP_NOT_FOUND);
    }
}
