<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateMonitoringSupportDevicesTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('monitoring_support_devices', function (Blueprint $table) {
            $table->uuid('id')->primary();
            $table->integer('number_of');
            $table->double('soil_temperature');
            $table->integer('soil_humidity');
            $table->integer('soil_ph');
            $table->double('soil_nitrogen');
            $table->double('soil_phosphor');
            $table->double('soil_kalium');
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
        Schema::dropIfExists('monitoring_support_devices');
    }
}
