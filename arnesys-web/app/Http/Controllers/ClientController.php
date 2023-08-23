<?php

namespace App\Http\Controllers;

use App\Mail\ClientRegistration;
use App\Models\Client;
use App\Models\Field;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use RealRashid\SweetAlert\Facades\Alert;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;

class ClientController extends Controller
{

    function index(){
        $clients = Client::all();

        return view('master.client.index', compact('clients'));
    }

    function create(){
        return view('master.client.create');
    }

    function store(Request $request){
        $validator = Validator::make($request->all(), [
            'username' => 'required',
            'email' => 'required',
            'first_name' => 'required',
            'last_name' => 'required',
            'phone' => 'required',
            'address' => 'required'
        ]);

        if($validator->fails()) {
            Alert::error('Failed', 'Client datas not valid');
            return redirect()->back()->withErrors($validator);
        }

        $password = Str::password(10);
        $details = [
            'name' => $request->first_name . ' ' . $request->last_name,
            'email' => $request->email,
            'password' => $password
        ];

        $client = new Client();
        $client->username = $request->username;
        $client->email = $request->email;
        $client->first_name = $request->first_name;
        $client->last_name = $request->last_name;
        $client->no_telp = $request->phone;
        $client->address = $request->address;
        $client->email_verified_at = now();
        $client->remember_token = Str::random(10);

        if($request->file('photo') != null){
            $photo = $request->file('photo');
            $pathPhoto = $photo->store('public/client');
            $client->photo = $pathPhoto;
        }

        Mail::to($request->email)->send(new ClientRegistration($details));

        $client->password = Hash::make($password);
        $client->save();

        Alert::success('Success', 'Client has been added');
        return redirect()->route('client');
    }

    function delete($id){
        Client::findOrFail($id)->delete();
        Field::where('client_id', $id)->delete();
        Alert::success('Success', 'Client has been deleted');
        return redirect()->route('client');
    }

}
