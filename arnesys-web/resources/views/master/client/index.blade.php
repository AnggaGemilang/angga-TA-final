@extends('layouts.master')

@section('title', 'Clients')

@section('breadcrumb-content')

    <nav aria-label="breadcrumb">
        <ol class="breadcrumb bg-transparent mb-0 pb-0 pt-1 px-0 me-sm-6 me-5">
            <li class="breadcrumb-item text-sm">
                <a class="opacity-5 text-white" href="javascript:;">Pages
                </a>
            </li>
            <li class="breadcrumb-item text-sm text-white active" aria-current="page">
                Client
            </li>
        </ol>
        <h6 class="font-weight-bolder text-white mb-0">Client</h6>
    </nav>

@endsection

@section('content')

    <div class="row content-wrapper mt-3">
        <div class="col-xl-12 col-sm-12">
            <div class="row">
                <div class="col-12">
                    <div class="card mb-4">
                        <div class="card-header pb-0">
                            <div class="row">
                                <div class="col">
                                    <h6>Clients Data</h6>
                                </div>
                                <div class="col text-end">
                                    <a href="{{ route('client.create') }}" class="btn btn-outline-success" style="float: right; margin-top: -5px;">Register New Client</a>
                                </div>
                            </div>
                        </div>
                        <div class="card-body px-0 pt-0 pb-2">
                            <div class="table-responsive p-0">
                                <table class="table align-items-center mb-0">
                                    <thead>
                                        <tr>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Name
                                            </th>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Client Code
                                            </th>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Email
                                            </th>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Client Created
                                            </th>
                                            <th class="text-center text-uppercase text-secondary text-xxs font-weight-bolder opacity-7">
                                                Action
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        @foreach ($clients as $row)
                                            <tr>
                                                <td>
                                                    <div class="d-flex px-2 py-1">
                                                        <div>
                                                            <img
                                                                @if ($row->photo == NULL)
                                                                    src="{{ asset('assets') }}/img/team-3.jpg"
                                                                @else
                                                                    src="{{ Storage::url($row->photo) }}"
                                                                @endif
                                                                class="avatar avatar-sm me-3" alt="user2">
                                                        </div>
                                                        <div class="d-flex flex-column justify-content-center">
                                                            <h6 class="mb-0 text-sm">{{ $row->first_name . ' ' . $row->last_name }}</h6>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td class="align-middle text-center text-sm">
                                                    <p class="text-xs text-secondary mb-0">{{ $row->id }}</p>
                                                </td>
                                                <td class="align-middle text-center text-sm">
                                                    <p class="text-xs text-secondary mb-0">{{ $row->email }}</p>
                                                </td>
                                                <td class="align-middle text-center">
                                                    <span class="text-secondary text-xs font-weight-bold">{{ $row->created_at }}</span>
                                                </td>
                                                <td class="align-middle">
                                                    <div class="d-flex justify-content-center">
                                                        <button type="button" class="btn btn-link text-success text-gradient px-3 mb-0 add-field" data-id="{{ $row->id }}"><i class="fas fa-plus me-2"></i>Add</button>
                                                        <form action="{{ route('client.delete', $row->id) }}" method="POST">
                                                            @csrf
                                                            @method('delete')
                                                            <button type="button" class="btn btn-link text-danger text-gradient px-3 mb-0 delete-btn"><i class="far fa-trash-alt me-2"></i>Delete</button>
                                                        </form>
                                                        <a class="btn btn-link text-dark text-gradient px-3 mb-0" href="{{ route('client.field', $row->id) }}"><i class="fas fa-arrow-right me-2"></i><span></span>Detail</a>
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

    <div class="modal fade" id="addNewFieldModal" tabindex="-1" role="dialog" aria-labelledby="modalSayaLabel"
        aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalLabel">Add Field</h5>
                    <span type="button" class="btnClose" style="font-size: 20px;">&times;</span>
                </div>
                <form action="{{ route('field.store') }}" method="POST" enctype="multipart/form-data">
                    @csrf
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="example-text-input" class="form-control-label">Address</label>
                            <textarea class="form-control" rows="4" name="address" placeholder="Enter address . . ."></textarea>
                        </div>
                        <div class="form-group">
                            <label for="plant-type">Plant Type</label>
                            <input type="text" class="form-control" id="plant_type" name="plant_type" placeholder="Enter plant type . . .">
                        </div>
                        <div class="form-group">
                            <label for="land_area">Land Area (ha)</label>
                            <input type="number" class="form-control" id="land_area" name="land_area" placeholder="Enter land area . . .">
                        </div>
                        <div class="form-group">
                            <label for="plantName">Number of Support Device</label>
                            <input type="number" class="form-control" id="number_of_support_device" name="number_of_support_device" placeholder="Enter number of support device . . .">
                        </div>
                        <div class="form-group">
                            <label for="thumbnail">Thumbnail</label>
                            <input type="file" class="form-control" id="thumbnail" name="thumbnail">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <input type="hidden" name="client_id" value="">
                        <button type="button" class="btnClose btn btn-secondary" data-dismiss="modal">Tutup</button>
                        <button type="submit" class="btn btn-success">Tambah Data</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
@endsection

@push('js')
<script>

    $('tr').on('click', '.delete-btn', function (e){
        e.preventDefault();
        swal({
            title: "Are you sure?",
            text: "Client would be deleted",
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

    $('tr').on('click', '.add-field', function() {
        $('input[name=client_id]').val($(this).attr('data-id'))
        $("#addNewFieldModal").modal("show")
    })

    $(".btnClose").click(function () {
        $("#addNewFieldModal").modal("hide")
    })

    $(document).ready(function() {
        closeLoader()
    })

</script>
@endpush
