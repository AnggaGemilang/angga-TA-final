<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\MonitoringSupportDevice;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class MonitoringSupportDeviceController extends Controller
{
    public function store(Request $request)
    {
        return MonitoringSupportDevice::create($request->all());
    }

    public function show($id, $number)
    {
        $monitoringSupportDevice = MonitoringSupportDevice::where('field_id', $id)
            ->where('number_of', $number)
            ->paginate(5);
        return $monitoringSupportDevice;
    }

    public function getChartData($id, $number, $column, $type)
    {
        $monitoringSupportDevice = [];
        if($type == "latest"){
            $monitoringSupportDevice = MonitoringSupportDevice::select($column . " AS value", "created_at", DB::raw('YEAR(created_at) AS time'))
                ->where('number_of', $number)
                ->where('field_id', $id)
                ->orderBy('created_at', 'DESC')
                ->paginate('10');
        } else if($type == "hour"){
            $monitoringSupportDevice = MonitoringSupportDevice::select($column . " AS value", 'created_at', DB::raw('HOUR(created_at) AS time'))
                ->where('number_of', $number)
                ->where('field_id', $id)
                ->groupBy(DB::raw('HOUR(created_at)'))
                ->paginate(10);
        } else {
            $monitoringSupportDevice = MonitoringSupportDevice::select($column . " AS value", 'created_at', DB::raw('DAY(created_at) AS time'))
                ->where('number_of', $number)
                ->where('field_id', $id)
                ->groupBy(DB::raw('DAY(created_at)'))
                ->paginate(10);
        }
        return $monitoringSupportDevice;
    }

}
