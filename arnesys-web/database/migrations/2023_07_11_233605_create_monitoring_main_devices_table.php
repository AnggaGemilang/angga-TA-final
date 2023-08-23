<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateMonitoringMainDevicesTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('monitoring_main_devices', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->double('wind_temperature');
            $table->integer('wind_humidity');
            $table->integer('light_intensity');
            $table->double('wind_pressure');
            $table->double('wind_speed');
            $table->boolean('rainfall');
            $table->string('field_id', 255);
            $table->index('field_id')->references('id')->on('fields')->onDelete('cascade');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('monitoring_main_devices');
    }
}
