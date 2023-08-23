@extends('layouts.master')

@section('title','Dashboard')

@section('breadcrumb-content')

<nav aria-label="breadcrumb">
    <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0 me-sm-6 me-5">
        <li class="breadcrumb-item text-sm"><a class="opacity-5 text-white"
                href="javascript:;">Pages</a></li>
        <li class="breadcrumb-item text-sm text-white active" aria-current="page">
            Dashboard
        </li>
    </ol>
    <h6 class="font-weight-bolder text-white mb-0">Dashboard</h6>
</nav>

@endsection

@section('content')

<div class="row content-wrapper mt-3" style="padding-bottom: 70px;">
    <div class="col-xl-12 col-sm-12">
        <div class="row">

            @hasrole('Operator')

                <div class="col-xl-4 col-sm-6">
                    <div class="card">
                        <div class="card-body p-3">
                            <div class="row">
                                <div class="col-9">
                                    <p class="text-md mb-0 text-capitalize font-weight-bold">Date and <br> Time</p>
                                </div>
                                <div class="col-3">
                                    <div class="icon icon-shape bg-gradient-danger shadow-success text-center rounded-circle" style="float: right;">
                                        <i class="ni ni-bulb-61 text-lg opacity-10" aria-hidden="true"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-12">
                                    <h4 id="txtDatetime" class="font-weight-bolder mt-2">
                                        . . .
                                    </h4>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-xl-4 col-sm-6 mb-xl-0 mb-4">
                    <div class="card">
                        <div class="card-body p-3">
                            <div class="row">
                                <div class="col-9">
                                    <p class="text-md mb-0 text-capitalize font-weight-bold">Number of <br> Clients</p>
                                </div>
                                <div class="col-3">
                                    <div class="icon icon-shape bg-gradient-warning shadow-success text-center rounded-circle" style="float: right;">
                                        <i class="ni ni-folder-17 text-lg opacity-10" aria-hidden="true"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-12">
                                    <h4 id="txtpH" class="font-weight-bolder mt-2">
                                        {{ $numberOfClients }}
                                    </h4>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-xl-4 col-sm-6">
                    <div class="card">
                        <div class="card-body p-3">
                            <div class="row">
                                <div class="col-9">
                                    <p class="text-md mb-0 text-capitalize font-weight-bold">Number of <br> Fields</p>
                                </div>
                                <div class="col-3">
                                    <div class="icon icon-shape bg-gradient-primary shadow-success text-center rounded-circle" style="float: right;">
                                        <i class="ni ni-support-16 text-lg opacity-10" aria-hidden="true"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-12">
                                    <h4 id="txtGas" class="font-weight-bolder mt-2">
                                        {{ $numberOfFields }}
                                    </h4>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            @endhasrole

            @hasrole('Client')

                <div class="col-xl-6 col-sm-6">
                    <div class="card">
                        <div class="card-body p-3">
                            <div class="row">
                                <div class="col-9">
                                    <p class="text-md mb-0 text-capitalize font-weight-bold">Date and <br> Time</p>
                                </div>
                                <div class="col-3">
                                    <div class="icon icon-shape bg-gradient-danger shadow-success text-center rounded-circle" style="float: right;">
                                        <i class="ni ni-bulb-61 text-lg opacity-10" aria-hidden="true"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-12">
                                    <h4 id="txtDatetime" class="font-weight-bolder mt-2">
                                        . . .
                                    </h4>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-xl-6 col-sm-6 mb-xl-0 mb-4">
                    <div class="card">
                        <div class="card-body p-3">
                            <div class="row">
                                <div class="col-9">
                                    <p class="text-md mb-0 text-capitalize font-weight-bold">Number of <br> Fields</p>
                                </div>
                                <div class="col-3">
                                    <div class="icon icon-shape bg-gradient-warning shadow-success text-center rounded-circle" style="float: right;">
                                        <i class="ni ni-folder-17 text-lg opacity-10" aria-hidden="true"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-12">
                                    <h4 id="txtpH" class="font-weight-bolder mt-2">
                                        {{ $numberOfFieldsByClient }}
                                    </h4>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            @endhasrole

        </div>
    </div>
</div>
@endsection

@push('js')
    <script>

        function display_ct7() {
            var x = new Date()
            var ampm = x.getHours( ) >= 12 ? ' PM' : ' AM';
            hours = x.getHours( ) % 12;
            hours = hours ? hours : 12;
            hours=hours.toString().length==1? 0+hours.toString() : hours;

            var minutes=x.getMinutes().toString()
            minutes=minutes.length==1 ? 0+minutes : minutes;

            var seconds=x.getSeconds().toString()
            seconds=seconds.length==1 ? 0+seconds : seconds;

            var month=(x.getMonth() +1).toString();
            month=month.length==1 ? 0+month : month;

            var dt=x.getDate().toString();
            dt=dt.length==1 ? 0+dt : dt;

            var x1=month + "/" + dt + "/" + x.getFullYear();
            x1 = x1 + ", " +  hours + ":" +  minutes + ":" +  seconds + " " + ampm;
            console.log(x1)
            $("#txtDatetime").text(x1)


            display_c7()
            closeLoader()
        }

        function display_c7(){
            var refresh = 1000;
            mytime = setTimeout('display_ct7()', refresh)
        }

        $(document).ready(function() {
            display_c7()
        })

    </script>
@endpush
