@extends('layouts.master')

@section('title', 'Fields')

@section('breadcrumb-content')
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0 me-sm-6 me-5">
            <li class="breadcrumb-item text-sm">
                <a class="opacity-5 text-white" href="javascript:;">
                    Pages
                </a>
            </li>
            @hasrole('Operator')
            <li class="breadcrumb-item text-sm text-white active" aria-current="page">
                <a class="opacity-5 text-white" href="{{ route('client') }}">
                    Client
                </a>
            </li>
            @endrole
            <li class="breadcrumb-item text-sm text-white active" aria-current="page">
                Field
            </li>
        </ol>
        <h6 class="font-weight-bolder text-white mb-0">Field </h6>
    </nav>
@endsection

@section('content')
    <div class="row content-wrapper mt-3" style="padding-bottom: 70px;">
        <div class="col-xl-12 col-sm-12">
            <div class="row">
                <div class="col-12">
                    <div class="card pb-3">
                        <div class="card-header pb-0">
                            <h6>Fields Data</h6>
                        </div>
                        <div class="card-body px-0 pt-0 pb-2">
                            <div class="table-responsive p-0">
                                <table class="table align-items-center mb-0">
                                    <thead>
                                        <tr>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Plant Name
                                            </th>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Field Code
                                            </th>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Land Area
                                            </th>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Address
                                            </th>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 text-center">
                                                Number of Support Device
                                            </th>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Action
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        @foreach ($fields as $row)
                                            <tr>
                                                <td>
                                                    <div class="d-flex px-2 py-1">
                                                        <div>
                                                            <img
                                                                @if ($row->thumbnail == NULL)
                                                                    src="{{ asset('assets') }}/img/field.jpg"
                                                                @else
                                                                    src="{{ Storage::url($row->thumbnail) }}"
                                                                @endif
                                                                class="avatar avatar-sm me-3" alt="user2">
                                                        </div>
                                                        <div class="d-flex flex-column justify-content-center">
                                                            <h6 class="mb-0 text-sm">{{ $row->plant_type }}</h6>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="d-flex justify-content-center">
                                                        <p class="text-xs font-weight-bold mb-0">{{ $row->id }}</p>
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="d-flex justify-content-center">
                                                        <p class="text-xs font-weight-bold mb-0">{{ $row->address }}</p>
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="d-flex justify-content-center">
                                                        <p class="text-xs font-weight-bold mb-0">{{ $row->land_area }}</p>
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="d-flex justify-content-center">
                                                        <p class="text-xs font-weight-bold mb-0">{{ $row->number_of_support_device }} Support Devices</p>
                                                    </div>
                                                </td>
                                                <td class="align-middle">
                                                    <div class="d-flex justify-content-center">

                                                        @hasrole('Operator')

                                                            <form action="{{ route('field.delete', $row->id) }}" method="POST">
                                                                @csrf
                                                                @method('delete')
                                                                <button type="button" class="btn btn-link text-danger text-gradient px-3 mb-0 delete-btn"><i class="far fa-trash-alt me-2"></i>Delete</button>
                                                            </form>

                                                        @endrole

                                                        <button class="btn btn-link detail-btn text-dark text-gradient px-3 mb-0">
                                                            <i class="fas fa-arrow-down me-2"></i>
                                                            <span>Detail</span>
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="5" style="padding: 0;">
                                                    <div class="detail-elements" style="padding: 10px;">
                                                        <table style="width: 100%;">
                                                            <tr>
                                                                <td colspan="5" style="padding-left: 15px;">
                                                                    <a href="{{ route('client.field.detail.main', $row->id) }}">
                                                                        <div class="pb-1 pt-1 icon-move-right">
                                                                            <span style="margin-bottom: 0; font-size: 15px;" class="link-dark text-bold">Perangkat Utama</span>
                                                                            <button class="btn btn-link btn-icon-only btn-rounded btn-sm text-dark my-auto"><i class="ni ni-bold-right" aria-hidden="true"></i></button>
                                                                        </div>
                                                                    </a>
                                                                </td>
                                                            </tr>

                                                            @for ($i = 0; $i < $row->number_of_support_device; $i++)
                                                                <tr>
                                                                    <td colspan="5" style="padding-left: 15px;">
                                                                        <a href="{{ route('client.field.detail.support', ['id' => $row->id, 'number' => $i+1]) }}">
                                                                            <div class="pb-1 pt-1 icon-move-right">
                                                                                <span style="margin-bottom: 0; font-size: 15px;" class="link-dark text-bold">Perangkat Pendukung {{ $i+1 }}</span>
                                                                                <button class="btn btn-link btn-icon-only btn-rounded btn-sm text-dark my-auto"><i class="ni ni-bold-right" aria-hidden="true"></i></button>
                                                                            </div>
                                                                        </a>
                                                                    </td>
                                                                </tr>
                                                            @endfor
                                                        </table>
                                                    </div>
                                                </td>
                                            </tr>
                                        @endforeach
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection

@push('style')

<style>
    .detail-elements {
        display: none;
    }
</style>

@endpush

@push('js')

<script>

    $('tr').on('click', '.delete-btn', function (e){
        e.preventDefault();
        swal({
            title: "Are you sure?",
            text: "Field would be deleted",
            icon: "warning",
            type: "warning",
            buttons: ["Cancel","Yes!"],
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, Delete!'
        }).then((willAccept) => {
            if (willAccept) {
                $(this).parent('form').trigger('submit')
            }
        });
    });

    $('tr').on('click', '.detail-btn', function() {
        $(this).closest('tr').next().find('.detail-elements').slideToggle(200, 'linear')
    });

    $(document).ready(function() {
        closeLoader()
    })

</script>

@endpush
