@extends('layouts.master')

@section('title', 'Detail Main Device')

@section('breadcrumb-content')
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0 me-sm-6 me-5">
            <li class="breadcrumb-item text-sm">
                <a class="opacity-5 text-white" href="javascript:;">Pages
                </a>
            </li>
            <li class="breadcrumb-item text-sm text-white active" aria-current="page">
                @hasrole('Operator')
                    <a class="opacity-5 text-white" href="{{ route('client.field', $field->client_id) }}">Field</a>
                @endrole
                @hasrole('Client')
                    <a class="opacity-5 text-white" href="{{ route('field') }}">Field</a>
                @endrole
            </li>
            <li class="breadcrumb-item text-sm text-white active" aria-current="page">
                Detail
            </li>
        </ol>
        <h6 class="font-weight-bolder text-white mb-0">Detail Support Device</h6>
    </nav>
@endsection

@section('content')
    <div class="row content-wrapper mt-3" style="padding-bottom: 70px;">
        <div class="col-xl-12 col-sm-12">
            <div class="row">
                <div class="col-12">
                    <div class="row">
                        <div class="col-xl-4 col-sm-6">
                            <div class="card">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">Temperature <br>
                                                    Value</p>
                                                <h4 id="txtSoilTemperature" class="font-weight-bolder mt-2">
                                                    25&deg;
                                                </h4>
                                            </div>
                                        </div>
                                        <div class="col-4 text-end">
                                            <div
                                                class="icon icon-shape bg-gradient-danger shadow-success text-center rounded-circle">
                                                <i class="ni ni-bulb-61 text-lg opacity-10" aria-hidden="true"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-xl-4 col-sm-6 mb-xl-0 mb-4">
                            <div class="card">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">Moisture <br> Value
                                                </p>
                                                <h4 id="txtSoilMoisture" class="font-weight-bolder mt-2">
                                                    40%
                                                </h4>
                                            </div>
                                        </div>
                                        <div class="col-4 text-end">
                                            <div
                                                class="icon icon-shape bg-gradient-warning shadow-success text-center rounded-circle">
                                                <i class="ni ni-folder-17 text-lg opacity-10" aria-hidden="true"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-xl-4 col-sm-6">
                            <div class="card">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">pH <br> Value
                                                </p>
                                                <h4 id="txtSoilPh" class="font-weight-bolder mt-2">
                                                    7
                                                </h4>
                                            </div>
                                        </div>
                                        <div class="col-4 text-end">
                                            <div
                                                class="icon icon-shape bg-gradient-primary shadow-success text-center rounded-circle">
                                                <i class="ni ni-support-16 text-lg opacity-10" aria-hidden="true"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row mt-4">
                        <div class="col-xl-4 col-sm-6">
                            <div class="card">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">Nitrogen <br>
                                                    Value</p>
                                                <h4 id="txtSoilNitrogen" class="font-weight-bolder mt-2">
                                                    3
                                                </h4>
                                            </div>
                                        </div>
                                        <div class="col-4 text-end">
                                            <div
                                                class="icon icon-shape bg-gradient-danger shadow-success text-center rounded-circle">
                                                <i class="ni ni-square-pin text-lg opacity-10" aria-hidden="true"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-xl-4 col-sm-6 mb-xl-0 mb-4">
                            <div class="card">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">Phosphor <br>
                                                    Value
                                                </p>
                                                <h4 id="txtSoilPhosphor" class="font-weight-bolder mt-2">
                                                    2
                                                </h4>
                                            </div>
                                        </div>
                                        <div class="col-4 text-end">
                                            <div
                                                class="icon icon-shape bg-gradient-warning shadow-success text-center rounded-circle">
                                                <i class="ni ni-paper-diploma text-lg opacity-10" aria-hidden="true"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-xl-4 col-sm-6">
                            <div class="card">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">Kalium <br>
                                                    Value
                                                </p>
                                                <h4 id="txtSoilKalium" class="font-weight-bolder mt-2">
                                                    2
                                                </h4>
                                            </div>
                                        </div>
                                        <div class="col-4 text-end">
                                            <div
                                                class="icon icon-shape bg-gradient-primary shadow-success text-center rounded-circle">
                                                <i class="ni ni-istanbul text-lg opacity-10" aria-hidden="true"></i>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row mt-4">
                <h5>Histories Data</h5>
            </div>
            <div class="row" style="margin-top: 15px;">
                <div class="col-xl-12 col-sm-12">
                    <nav>
                        <div class="nav nav-tabs nav-fill" id="nav-tab" role="tablist">
                            <a class="nav-item nav-link active" href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="soil_temperature">Warmth</span>
                            </a>
                            <a class="nav-item nav-link " href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="soil_humidity">Moisture</span>
                            </a>
                            <a class="nav-item nav-link " href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="soil_ph">pH</span>
                            </a>
                            <a class="nav-item nav-link " href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="soil_nitrogen">Nitrogen</span>
                            </a>
                            <a class="nav-item nav-link " href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="soil_phosphor">Phosphor</span>
                            </a>
                            <a class="nav-item nav-link " href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="soil_kalium">Kalium</span>
                            </a>
                        </div>
                    </nav>
                    <div class="tab-content">
                        <div class="tab-pane active bg-white" id="1">
                            <div class="row">
                                <div class="col">
                                    <h4 id="valDataType">Warmth</h4>
                                </div>
                                <div class="col d-flex justify-content-end" style="margin-right: 15px;">
                                    <select class="form-select" id="filter-history" style="width: 150px;">
                                        <option value="latest" selected>Latest</option>
                                        <option value="hour">Per Hour</option>
                                        <option value="day">Per Day</option>
                                    </select>
                                </div>
                            </div>
                            <canvas id="chart"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

@endsection

@push('style')
    <style>
        .tab-content {
            border-bottom: 1px solid #DEE2E6;
            border-left: 1px solid #DEE2E6;
            border-right: 1px solid #DEE2E6;
        }

        .tab-pane {
            padding-left: 75px;
            padding-right: 60px;
            padding-top: 50px;
            padding-bottom: 50px;
        }
    </style>
@endpush

@push('js')
    <script src="https://cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.min.js" type="text/javascript"></script>
    <script src="{{ asset('assets/js/mqtt.js') }}"></script>

    <script>

        var myChart
        var ctx = document.getElementById("chart")
        var fieldId =  "<?php echo $field->id; ?>"
        var title = "Warmth"
        var column = "soil_temperature"
        var number = "1"
        var type = "latest"

        function getDataChart(fieldId, number, column, type, title){
            $.ajax({
                url:"/api/monitoring-support-devices/get-chart/" + fieldId +  "/" + number + "/" + column + "/" + type,
                type:"GET",
                success: function(datas) {
                    showChart(title, datas)
                }
            });
        }

        function onMessageArrived(r_message){
            closeLoader()

            out_msg = "Message received "+ r_message.payloadString + "<br>"
            out_msg=out_msg+"Message received Topic "+r_message.destinationName

            var topic = r_message.destinationName

            if(topic==`arnesys/${fieldId}/pendukung/1`){
                var data = JSON.parse(r_message.payloadString)
                $("#txtSoilTemperature").text(data.monitoring.soil_temperature + "°")
                $("#txtSoilMoisture").text(data.monitoring.soil_humidity + "%")
                $("#txtSoilPh").text(data.monitoring.soil_ph)
                $("#txtSoilNitrogen").text(data.monitoring.soil_nitrogen + " mg/kg")
                $("#txtSoilPhosphor").text(data.monitoring.soil_phosphor + " mg/kg")
                $("#txtSoilKalium").text(data.monitoring.soil_kalium + " mg/kg")
            }
        }

        function showChart(title, datas){
            myChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: datas.data.map(function(a){
                        const dateFormat = new Date(a.created_at)
                        return dateFormat.getDate()+
                            "/"+(dateFormat.getMonth()+1)+
                            "/"+dateFormat.getFullYear()+
                            " "+dateFormat.getHours()+
                            ":"+dateFormat.getMinutes()+
                            ":"+dateFormat.getSeconds();
                    }),
                    datasets: [{
                        label: "Histories of " + title,
                        data: datas.data.map(a => a.value),
                        backgroundColor: [
                            '#66BB6A',
                        ],
                        borderColor: [
                            '#66BB6A',
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    animation: false,
                    scales: {
                        x: {
                            ticks: {
                                maxRotation: 45,
                                minRotation: 45
                            }
                        }
                    }
                }
            })
        }

        $(".nav-link").click(function() {
            openLoader()
            title = $(this).find("span").text()
            column = $(this).find("span").attr("data-column")
            $("#valDataType").text(title)
            myChart.destroy()
            getDataChart(fieldId, number, column, type, title)
        })

        $("#filter-history").change(function() {
            openLoader()
            type = $(this).val()
            myChart.destroy()
            getDataChart(fieldId, number, column, type, title)
        })

        $(document).ready(function() {
            MQTTconnect()
            getDataChart(fieldId, number, column, type, title)
        })

    </script>
@endpush
