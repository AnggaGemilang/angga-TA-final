@extends('layouts.master')

@section('title', 'Detail Main Device')

@section('breadcrumb-content')
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0 me-sm-6 me-5">
            <li class="breadcrumb-item text-sm">
                <a class="opacity-5 text-white" href="javascript:;">Pages</a>
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
        <h6 class="font-weight-bolder text-white mb-0">Detail Main Device</h6>
    </nav>
@endsection

@section('content')
    <div class="row content-wrapper mt-3" style="padding-bottom: 70px;">
        <div class="col-xl-12 col-sm-12">
            <div class="row">
                <div class="col-12">
                    <div class="alert alert-success" style="color: white; border: none;" role="alert">
                        <b>Congrats</b>, Field Safe
                    </div>
                    <div class="alert alert-danger" style="color: white; border: none;" role="alert">
                        <ul class="alert-list"></ul>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-4">
                    <div class="card">
                        <div class="card-body p-3">
                            <div class="row" style="height: 258px;">
                                <div class="col-7" style="padding-right: 0px !important;">
                                    <div class="row">
                                        <p class="mb-0 text-capitalize font-weight-bold" style="font-size: 13pt;">Weather
                                            <br>
                                            Condition
                                        </p>
                                    </div>
                                    <div class="row" style="align-items: center; height: 78%;">
                                        <img class="weatherForecast" src="{{ asset('assets') }}/img/sun-cloud.png"
                                            alt="" style="width: 230px; margin-left: -12px;">
                                    </div>
                                </div>
                                <div class="col-5 text-end" style="padding-left: 0px !important;">
                                    <div
                                        class="icon icon-shape bg-gradient-success shadow-success text-center rounded-circle">
                                        <i class="ni ni-bell-55 text-lg opacity-10" aria-hidden="true"></i>
                                    </div>
                                    <p class="mb-0 text-capitalize" style="margin-top: 48px; font-size: 11pt;">
                                        Wind Speed
                                    </p>
                                    <h4 id="txtWindSpeed" class="mb-0 font-weight-bolder" style="font-size: 14pt;">
                                        7 mph
                                    </h4>
                                    <p class="mb-0 text-capitalize" style="font-size: 11pt; margin-top: 15px;">
                                        Wind Pressure
                                    </p>
                                    <h4 id="txtWindPressure" class="mb-0 text-capitalize font-weight-bolder"
                                        style="font-size: 14pt;">
                                        13
                                    </h4>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-8">
                    <div class="row">
                        <div class="col-xl-6 col-sm-6">
                            <div class="card">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">Warmth <br>
                                                    Value</p>
                                                <h4 id="txtWindTemperature" class="font-weight-bolder mt-2">
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
                        <div class="col-xl-6 col-sm-6">
                            <div class="card">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">Humidity <br> Value
                                                </p>
                                                <h4 id="txtHumidity" class="font-weight-bolder mt-2">
                                                    20%
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
                    </div>
                    <div class="row mt-4">
                        <div class="col-xl-6 col-sm-6">
                            <div class="card pests-wrapper">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">Pests <br>
                                                    Forecast</p>
                                                <h4 id="txtPest" class="font-weight-bolder mt-2">
                                                    Safe
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
                        <div class="col-xl-6 col-sm-6">
                            <div class="card">
                                <div class="card-body p-3">
                                    <div class="row">
                                        <div class="col-8">
                                            <div class="numbers" style="min-height: 93px;">
                                                <p class="text-md mb-0 text-capitalize font-weight-bold">Light <br>
                                                    Intensity
                                                </p>
                                                <h4 id="txtLightIntensity" class="font-weight-bolder mt-2">
                                                    1000
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
                                <span class="text-success text-bold size" data-column="wind_temperature">Warmth</span>
                            </a>
                            <a class="nav-item nav-link " href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="wind_humidity">Humidity</span>
                            </a>
                            <a class="nav-item nav-link " href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="wind_speed">Wind Speed</span>
                            </a>
                            <a class="nav-item nav-link " href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="wind_pressure">Wind Pressure</span>
                            </a>
                            <a class="nav-item nav-link " href="#1" role="tab" aria-selected="false"
                                data-toggle="tab">
                                <span class="text-success text-bold size" data-column="light_intensity">Light
                                    Intensity</span>
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

    <div class="modal fade" id="seekPestsPrediction" tabindex="-1" role="dialog" aria-labelledby="modalSayaLabel"
        aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalLabel">Detail Pests Prediction</h5>
                    <span type="button" class="btnClose" style="font-size: 20px;">&times;</span>
                </div>
                <div class="modal-body">

                    <div class="row item-pest-wrapper">
                        <div class="col-2 d-flex justify-content-center align-items-center">
                            <div class="icon icon-shape bg-gradient-success shadow-success text-center rounded-circle">
                                <i class="ni ni-map-big text-lg opacity-10" aria-hidden="true"></i>
                            </div>
                        </div>
                        <div class="col-10 d-flex align-items-center">
                            <div class="row">
                                <div class="col">
                                    <div class="row">
                                        <h6 style="font-size: 14pt;" class="mb-0">Ulat Daun</h6>
                                    </div>
                                    <div class="row">
                                        <p id="txtUlatDaun" style="font-size: 12pt;" class="mb-0">Tinggi</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row item-pest-wrapper mt-3">
                        <div class="col-2 d-flex justify-content-center align-items-center">
                            <div class="icon icon-shape bg-gradient-success shadow-success text-center rounded-circle">
                                <i class="ni ni-map-big text-lg opacity-10" aria-hidden="true"></i>
                            </div>
                        </div>
                        <div class="col-10 d-flex align-items-center">
                            <div class="row">
                                <div class="col">
                                    <div class="row">
                                        <h6 style="font-size: 14pt;" class="mb-0">Ulat Krop</h6>
                                    </div>
                                    <div class="row">
                                        <p id="txtUlatKrop" style="font-size: 12pt;" class="mb-0">Tinggi</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row item-pest-wrapper mt-3">
                        <div class="col-2 d-flex justify-content-center align-items-center">
                            <div class="icon icon-shape bg-gradient-success shadow-success text-center rounded-circle">
                                <i class="ni ni-map-big text-lg opacity-10" aria-hidden="true"></i>
                            </div>
                        </div>
                        <div class="col-10 d-flex align-items-center">
                            <div class="row">
                                <div class="col">
                                    <div class="row">
                                        <h6 style="font-size: 14pt;" class="mb-0">Busuk Hitam</h6>
                                    </div>
                                    <div class="row">
                                        <p id="txtBusukHitam" style="font-size: 12pt;" class="mb-0">Tinggi</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btnClose btn btn-secondary" data-dismiss="modal">Tutup</button>
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

        .pests-wrapper:hover {
            cursor: pointer;
            background: rgb(250, 250, 250);
            transition: .5s;
        }

        .item-pest-wrapper {
            background: rgb(250, 250, 250);
            border-radius: 10px;
            margin-left: 5px;
            margin-right: 5px;
            padding: 10px 0px;
        }

        .alert-success, .alert-danger {
            display: none;
        }

        .alert-list {
            margin-bottom: 0px;
        }

    </style>
@endpush

@push('js')
    <script src="https://cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.min.js" type="text/javascript"></script>
    <script src="{{ asset('assets/js/mqtt.js') }}"></script>

    <script>
        var myChart
        var ctx = document.getElementById("chart")
        var clientId = "<?php echo $field->client_id; ?>"
        var fieldId = "<?php echo $field->id; ?>"
        var title = "Warmth"
        var column = "wind_temperature"
        var type = "latest"
        var pestsPrediction = [];

        $(document).ready(function() {
            getDataChart(fieldId, column, type, title)
            MQTTconnect()
        })

        function onMessageArrived(r_message) {
            out_msg = "Message received " + r_message.payloadString + "<br>"
            out_msg = out_msg + "Message received Topic " + r_message.destinationName

            var topic = r_message.destinationName

            if (topic == `arnesys/${fieldId}/utama`) {
                var data = JSON.parse(r_message.payloadString)
                $("#txtWindSpeed").text(data.monitoring.wind_speed + " knot")
                $("#txtWindPressure").text(data.monitoring.wind_pressure + " hPa")
                $("#txtWindTemperature").text(data.monitoring.wind_temperature + "°")
                $("#txtWindHumidity").text(data.monitoring.wind_humidity + "%")
                $("#txtLightIntensity").text(data.monitoring.light_intensity + " lux")
            } else if (topic == `arnesys/${fieldId}/pendukung/1`) {
                var data = JSON.parse(r_message.payloadString)

                $(".alert-list").empty()

                if(data != null){
                    if(data.monitoring.soil_nitrogen < 4){
                        $(".alert-danger").show()
                        $(".alert-list").append("<li><b>Kekurangan Nitrogen</b>, Memberikan pupuk organic, seperti kotoran sapi, kotoran hewan, serbuk gergaji, atau menambahkan bakteri salah satunya azotobacter, Anabaena, dll.</li>")
                    }

                    if(data.monitoring.soil_phosphor < 4){
                        $(".alert-danger").show()
                        $(".alert-list").append("<li><b>Kekurangan Phosphor</b>, Memberikan abu sekam padi 30%</li>")
                    }

                    if(data.monitoring.soil_kalium < 5){
                        $(".alert-danger").show()
                        $(".alert-list").append("<li><b>Kekurangan Kalium</b>, Memberikan abu kayu, cangkang telur (disemprotkan pada daun), atau tepung tulang</li>")
                    }

                    if(data.monitoring.soil_ph < 5){
                        $(".alert-danger").show()
                        $(".alert-list").append("<li><b>Kekurangan pH</b>, Memberikan abu kayu, atau dengan mikroorganisme seperti EM4</li>")
                    }

                    if(data.monitoring.soil_ph > 8){
                        $(".alert-danger").show()
                        $(".alert-list").append("<li><b>Kelebihan pH</b>, Memberikan belerang, sulfur atau serbuk kayu</li>")
                    }

                    if($(".alert-list").children().length == 0){
                        $(".alert-success").show()
                    }
                }
            } else if (topic == `arnesys/${fieldId}/utama/ai`) {
                var data = JSON.parse(r_message.payloadString)
                var weatherForecast = data.ai_processing.weather_forecast

                pestsPrediction = data.ai_processing.pests_prediction.split(",")

                var status = "Safe"
                for (var i = 0; i < pestsPrediction.length; i++) {
                    var item = pestsPrediction[i].split("=")
                    if (item[1] == "tinggi") {
                        status = "Risky"
                    }
                }
                $("#txtPest").text(status)

                if (weatherForecast == "Cerah-Berawan") {
                    $(".weatherForecast").attr("src", "{{ asset('assets/img/sun-cloud.png') }}")
                } else if (weatherForecast == "Hujan Ringan") {
                    $(".weatherForecast").attr("src", "{{ asset('assets/img/rain.png') }}")
                } else {
                    $(".weatherForecast").attr("src", "{{ asset('assets/img/rain2.png') }}")
                }
                closeLoader()
            }
        }

        function getDataChart(fieldId, column, filterType, title) {
            $.ajax({
                url: "/api/monitoring-main-devices/get-chart/" + fieldId + "/" + column + "/" + filterType,
                type: "GET",
                success: function(datas) {
                    showChart(title, datas)
                }
            });
        }

        $('.pests-wrapper').click(function() {
            var ulatDaunVal = pestsPrediction[0].split("=")[1]
            $("#txtUlatDaun").text(ulatDaunVal.charAt(0).toUpperCase() + ulatDaunVal.slice(1))
            var ulatKropVal = pestsPrediction[1].split("=")[1]
            $("#txtUlatKrop").text(ulatKropVal.charAt(0).toUpperCase() + ulatKropVal.slice(1))
            var busukHitamVal = pestsPrediction[2].split("=")[1]
            $("#txtBusukHitam").text(busukHitamVal.charAt(0).toUpperCase() + busukHitamVal.slice(1))
            $("#seekPestsPrediction").modal("show")
        })

        $(".nav-link").click(function() {
            openLoader()
            title = $(this).find("span").text()
            column = $(this).find("span").attr("data-column")
            $("#valDataType").text(title)
            myChart.destroy()
            getDataChart(fieldId, column, type, title)
        })

        $("#filter-history").change(function() {
            openLoader()
            type = $(this).val()
            myChart.destroy()
            getDataChart(fieldId, column, type, title)
        })

        function showChart(title, datas) {
            myChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: datas.data.map(function(a) {
                        const dateFormat = new Date(a.created_at)
                        return dateFormat.getDate() +
                            "/" + (dateFormat.getMonth() + 1) +
                            "/" + dateFormat.getFullYear() +
                            " " + dateFormat.getHours() +
                            ":" + dateFormat.getMinutes() +
                            ":" + dateFormat.getSeconds();
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
    </script>
@endpush
