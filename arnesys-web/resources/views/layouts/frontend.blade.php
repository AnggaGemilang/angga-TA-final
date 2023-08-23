<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8" />
    <meta name="csrf-token" content="{{ csrf_token() }}">
    <meta name="viewport"
        content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0" />
    <meta name="description" content="" />
    <title>Arnesys - Overview</title>
    <link rel="icon" type="image/png" href="{{ asset('assets') }}/img/favicon.png">
    <link rel="stylesheet" type="text/css" href="{{ asset('assets') }}/landing-page/css/responsive.css" />
    <link rel="stylesheet" type="text/css" href="{{ asset('assets') }}/landing-page/css/bootstrap.css" />
    <link rel="stylesheet" type="text/css" href="{{ asset('assets') }}/landing-page/css/style.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.3/font/bootstrap-icons.css">

</head>

<body>
    <div class="hero_area">
        <div class="container-fluid" id="nav-on-scrolled">
            <div class="container">
                <nav class="navbar navbar-expand-lg custom_nav-container">
                    <a class="navbar-brand" href="#">
                        {{-- <img src="{{ asset("assets") }}/landing-page/images/logo.png" style="width: 150px;" alt=""> --}}
                        <p style="color: black; font-weight: 700; font-size: 20pt; margin-top: 17px;">Arnesys</p>
                    </a>
                    <a class="navbar-toggler btn btn-outline-success mt-2" href="{{ route('dashboard') }}"
                        style="border-radius: 5px; border: 1px solid #3BAF56; padding-bottom: 13px; padding-top: 10px;">
                        <i class="bi bi-person text-success"></i>
                        <span class="ml-2 text-success">{{ Auth::user() ? 'Dashboard' : 'Login' }}</span>
                    </a>
                    <div class="collapse navbar-collapse" style="padding-top: 5px; padding-left: 20px;"
                        id="navbarSupportedContent">
                        <ul class="navbar-nav mr-auto">
                            <li class="nav-item">
                                <a class="nav-link" href="#technologies-section">Technologies</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#about-section">About</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#features-section">Features</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#team-section">Team</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#contact-section">Contact</a>
                            </li>
                        </ul>
                        <a href="{{ route('dashboard') }}" class="btn btn-outline-success my-2 my-sm-0 pl-3 pr-3">
                            <i class="bi bi-person"></i>
                            <span class="ml-2">{{ Auth::user() ? 'Dashboard' : 'Login' }}</span>
                        </a>
                    </div>
                </nav>
            </div>
        </div>

        <header class="header_section">
            <div class="container">
                <nav class="navbar navbar-expand-lg custom_nav-container pt-5">
                    <a class="navbar-brand" href="#">
                        {{-- <img src="{{ asset("assets") }}/landing-page/images/logo.png" style="width: 150px;" alt=""> --}}
                        <p style="color: black; font-weight: 700; font-size: 20pt; margin-top: 37px;">Arnesys</p>
                    </a>
                    <a class="navbar-toggler btn btn-outline-light mt-2" href="{{ route('dashboard') }}"
                        style="border-radius: 5px; border: 1px solid white; padding-bottom: 13px; padding-top: 10px;">
                        <i class="bi bi-person text-light"></i>
                        <span class="ml-2 text-light">{{ Auth::user() ? 'Dashboard' : 'Login' }}</span>
                    </a>
                    <div class="collapse navbar-collapse" style="padding-top: 25px; padding-left: 20px;"
                        id="navbarSupportedContent">
                        <ul class="navbar-nav mr-auto">
                            <li class="nav-item">
                                <a class="nav-link" href="#technologies-section">Technologies</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#about-section">About</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#features-section">Features</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#team-section">Team</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#contact-section">Contact</a>
                            </li>
                        </ul>
                        <a href="{{ route('dashboard') }}" class="btn btn-outline-light my-2 my-sm-0 pl-3 pr-3">
                            <i class="bi bi-person"></i>
                            <span class="ml-2">{{ Auth::user() ? 'Dashboard' : 'Login' }}</span>
                        </a>
                    </div>
                </nav>
            </div>
        </header>
        <section class="slider_section mt-3">
            <div class="container">
                <div class="row">
                    <div class="col-md-6" style="position: relative; z-index: 9;">
                        <div class="detail-box header">
                            <h1>
                                <b>
                                    Proudly Present <br>
                                    Arnesys
                                </b>
                            </h1>
                            <p>
                                An innovation in the field of agriculture that can monitor various parameters in the air
                                and soil, predict rainfall, and foresee diseases and pests that will appear
                            </p>
                            <div class="btn-box">
                                <a href="#technologies-section" class="btn-1">
                                    Read More
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="img-box" style="position: absolute; top: -100px; right: 0;">
                        <img src="{{ asset('assets') }}/landing-page/images/img-header.png" alt=""
                            style="width: 580px; z-index: -1;" />
                    </div>
                </div>
            </div>
        </section>
    </div>
    <section class="about_section layout_padding reveal" id="technologies-section">
        <div class="img-box" style="position: relative;">
            <img src="{{ asset('assets') }}/landing-page/images/daun.png" alt=""
                style="width: 150px; position: absolute; left: 0; top: -250px; z-index: -1;" />
        </div>
        <div class="container-fluid" style="background: url('assets/landing-page/images/bg-top.png');">
            <div class="row d-flex justify-content-center">
                <h3><b>Applied Technologies</b></h3>
            </div>
            <div class="row d-flex justify-content-center pt-2">
                <p class="text-center">We provide convenience to our users by using <br>
                    the latest technologies nowadays</p>
            </div>
            <div class="container" style="padding-top: 75px;">
                <div class="row">
                    <div class="col-md-3">
                        <div class="card pl-3 pr-3">
                            <div class="card-body">
                                <div class="row">
                                    <i style="font-size: 50pt;" class="bi bi-wifi text-success"></i>
                                </div>
                                <div class="row mt-3">
                                    <h5><b>Internet of Things</b></h5>
                                </div>
                                <div class="row">
                                    <p>
                                        Using this technology allows users to remotely monitor and control through
                                        internet intermediaries using the official website and mobile applications that
                                        we have provided.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card pl-3 pr-3">
                            <div class="card-body">
                                <div class="row">
                                    <i style="font-size: 50pt;" class="bi bi-stars text-success"></i>
                                </div>
                                <div class="row mt-3">
                                    <h5><b>Smart System</b></h5>
                                </div>
                                <div class="row">
                                    <p>
                                        Implement Artificial Intelligence technology, especially Fuzzy Logic and Naive
                                        Baiyes algorithm that allow user to know weather also pests and diseases
                                        prediction .
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card pl-3 pr-3">
                            <div class="card-body">
                                <div class="row">
                                    <i style="font-size: 50pt;" class="bi bi-brightness-high text-success"></i>
                                </div>
                                <div class="row mt-3">
                                    <h5><b>Solar Panel</b></h5>
                                </div>
                                <div class="row">
                                    <p>
                                        Solar panels power Arnesys by converting sunlight into electricity. By utilizing
                                        solar energy, make it can operate independently in remote or off-grid locations
                                        expanding the range scope.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card pl-3 pr-3">
                            <div class="card-body">
                                <div class="row">
                                    <i style="font-size: 50pt;" class="bi bi-stars text-success"></i>
                                </div>
                                <div class="row mt-3">
                                    <h5><b>LoRA</b></h5>
                                </div>
                                <div class="row">
                                    <p>
                                        LoRa enables efficient connectivity for Arnesys, allowing them to transmit data
                                        over long distances while consuming minimal energy with some sources of node
                                        component.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="about_section layout_padding reveal" id="about-section">
        <div class="container">
            <div class="row" style="background: url('{{ asset('assets') }}/')">
                <div class="col-md-6">
                    <div class="detail-box">
                        <div class="heading_container">
                            <h1>
                                <b>What Is Arnesys?</b>
                            </h1>
                        </div>
                        <p>
                            Arnesys is an IoT-based monitoring system for horticultural farming that incorporates LoRa
                            technology and Artificial Intelligence (AI) to provide efficient and intelligent monitoring
                            capabilities.
                            Equipped with various sensors, the device continuously collects and transmits data such as
                            soil moisture, temperature, air humidity, and light intensity to an IoT platform.
                            By leveraging the wide-range coverage of LoRa network, the device enables seamless
                            communication between the sensors and the central monitoring system, even in remote
                            agricultural areas.
                            The collected data is then processed and analyzed using AI algorithms, allowing farmers to
                            gain valuable insights into crop conditions, optimize resource allocation, and make
                            data-driven decisions.
                            This integrated solution enhances precision agriculture practices, enabling farmers to
                            remotely monitor and manage horticultural crops with increased efficiency, reduced costs,
                            and improved yields.
                        </p>
                    </div>
                </div>
                <div class="col-md-6 d-flex justify-content-center" style="position: relative;">
                    <div class="img-box">
                        <img src="{{ asset('assets') }}/landing-page/images/arnesys.png" alt=""
                            style="width: 580px; z-index: -1;" />
                    </div>
                </div>
            </div>
            <div class="row mt-4">
                <div class="col-md-8">
                    <div class="row">
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-layers" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <h4>Convenience</h4>
                                    <p>
                                        Arnesys is designed to make it easier for users, so it is supported by the
                                        latest technologies
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-shuffle" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <h4>Flexibility</h4>
                                    <p>Arnesys is very flexible because it can be accessed by users through website
                                        and mobile applications</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row mt-3">
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-layers" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <h4>Reliability</h4>
                                    <p>
                                        Arnesys has many parameters so that it can produce maximum output for the
                                        needs of a plant
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi-box-seam" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <h4>Portable</h4>
                                    <p>
                                        Arnesys is designed to have a high level of portability, so that this tool can
                                        be assembled and carried anywhere easily
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="vl" style="float: left;"></div>
                    <div class="pl-5" style="margin-top: 80px;">
                        <a href="{{ route('dashboard') }}" class="btn btn-success w-100">
                            <i class="bi bi-user"></i>
                            <span>{{ Auth::user() ? 'Dashboard' : 'Login' }}</span>
                        </a>
                        <p class="mt-3 text-center">Or download to mobile device</p>
                        <div class="btn-play-store"
                            style="display: flex; justify-content: center; align-items: center;">
                            <a style="background: #f6f6f6; color: rgba(0, 0, 0, 0.8); width: 220px; border-radius: 10px; display: flex; justify-content: center; align-items: center;"
                                href="https://play.google.com/store/apps/details?id=com.agrapana.Arnesys&hl=en_US"
                                target="_blank">
                                <i class="bi bi-google-play" style="font-size: 20pt;"></i><br>
                                <p style="margin-top: 15px; padding-left: 10px;">Google Play Store</p>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="about_section layout_padding reveal" id="features-section">
        <div class="img-box" style="position: relative;">
            <img src="{{ asset('assets') }}/landing-page/images/daun2.png" alt=""
                style="width: 150px; position: absolute; right: 0; top: -250px; z-index: -1;" />
        </div>
        <div class="container">
            <div class="row" style="background: url('{{ asset('assets') }}/')">
                <div class="col-md-6 d-flex justify-content-center" style="position: relative;">
                    <div class="img-box">
                        <img src="{{ asset('assets') }}/landing-page/images/arnesys.png" alt=""
                            style="width: 580px; z-index: -1; margin-left: -50px;" />
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="detail-box">
                        <div class="heading_container">
                            <h1>
                                <b>What Does Arnesys Do?</b>
                            </h1>
                        </div>
                        <p>
                            Arnesys is an IoT-based monitoring system for horticultural farming that incorporates LoRa
                            technology and Artificial Intelligence (AI) to provide efficient and intelligent monitoring
                            capabilities.
                            Equipped with various sensors, the device continuously collects and transmits data such as
                            soil moisture, temperature, air humidity, and light intensity to an IoT platform.
                            By leveraging the wide-range coverage of LoRa network, the device enables seamless
                            communication between the sensors and the central monitoring system, even in remote
                            agricultural areas.
                            The collected data is then processed and analyzed using AI algorithms, allowing farmers to
                            gain valuable insights into crop conditions, optimize resource allocation, and make
                            data-driven decisions.
                            This integrated solution enhances precision agriculture practices, enabling farmers to
                            remotely monitor and manage horticultural crops with increased efficiency, reduced costs,
                            and improved yields.
                        </p>
                    </div>
                </div>
            </div>
            <div class="row mt-5">
                <div class="col-md-8">
                    <div class="row">
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-check2-circle" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <p>
                                        Make it easier for farmers to monitor the condition of agricultural land.
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-check2-circle" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <p>
                                        Arnesys has the ability to monitor soil and air conditions.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-check2-circle" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <p>
                                        Arnesys allows users to get weather, and pest prediction results on field conditions.
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-check2-circle" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <p>
                                        Arnesys is equipped with technology in the form of solar panels to help supply electricity.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-check2-circle" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <p>
                                        Arnesys is equipped with technology in the form of Lora to monitor whatever area of land a farmer has.
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-check2-circle" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <p>
                                        Arnesys is equipped with technology in the form of AI with the Fuzzy and Naive Bayes methods to make predictions.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-check2-circle" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <p>
                                        Users can use the website platform or mobile application to the account.
                                    </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div style="display: flex;">
                                <i class="icon-features bi bi-check2-circle" aria-hidden="true"></i>
                                <div style="margin-left: 20px;">
                                    <p>
                                        Arnesys is designed to be portable so that it is easy to carry and move.
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="vl" style="float: left; height: 350px;"></div>
                    <div class="ml-5" style="padding: 10px 30px; background: #f6f6f6; border-radius: 10px;">
                        <hr style="width: 120px; height: 0; border: 0; border-bottom: 4px solid #66BB6A;">
                        <p>
                            Arnesys is expected to become a tool that is able to provide solutions to various problems
                            related to agriculture that occur in general in the world, and specifically in Indonesia.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="about_section layout_padding reveal" id="team-section">
        <div class="img-box" style="position: relative;">
            <img src="{{ asset('assets') }}/landing-page/images/daun.png" alt=""
                style="width: 150px; position: absolute; left: 0; top: -250px; z-index: -1;" />
        </div>
        <div class="container">
            <div class="row d-flex justify-content-center">
                <h3><b>Meet Our Team</b></h3>
            </div>
            <div class="row d-flex justify-content-center pt-2">
                <p class="text-center">They are the people behind the scenes who work hard, <br> hopefully always
                    healthy and long life</p>
            </div>
            <div class="row mt-3">
                <div class="col-md-4" style="display: flex; flex-direction: column; justify-content: space-between;">
                    <div class="member-item" data-id="1">
                        <h4>Maisevli Harika</h4>
                        <p style="margin-top: -5px;">Supervisor</p>
                    </div>
                    <div class="member-item" data-id="2">
                        <h4>Angga Gemilang</h4>
                        <p style="margin-top: -5px;">Application and Network Developer</p>
                    </div>
                    <div class="member-item" data-id="3">
                        <h4>Indra Aolia Nugraha</h4>
                        <p style="margin-top: -5px;">Electrical Designer and Controls</p>
                    </div>
                    <div class="member-item" data-id="4">
                        <h4>Ghaitsa Inayatur Rahma</h4>
                        <p style="margin-top: -5px;">Agricultural Experts and Analyzers</p>
                    </div>
                </div>
                <div class="col-md-8">
                    <div class="row">
                        <div class="col-md-12">
                            <div class="gallery">
                                <figure id="figure-item" class="gallery__item" data-id="1">
                                    <img src="{{ asset('assets') }}/landing-page/images/maisevli.png"
                                        class="gallery__img" alt="Image 1">
                                </figure>
                                <figure id="figure-item" class="gallery__item" data-id="2">
                                    <img src="{{ asset('assets') }}/landing-page/images/angga.png"
                                        class="gallery__img" alt="Image 2">
                                </figure>
                                <figure id="figure-item" class="gallery__item" data-id="3">
                                    <img src="{{ asset('assets') }}/landing-page/images/indra2.png"
                                        class="gallery__img" alt="Image 3">
                                </figure>
                                <figure id="figure-item" class="gallery__item" data-id="4">
                                    <img src="{{ asset('assets') }}/landing-page/images/ghaitsa2.png"
                                        class="gallery__img" alt="Image 4">
                                </figure>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="contact_section pt-5 pb-5 reveal" id="contact-section">
        <div class="container">
            <div class="row">
                <div class="col-md-5">
                    <div class="form_container">
                        <div class="heading_container">
                            <h2>
                                Contact Us
                            </h2>
                        </div>
                        <form action="#">
                            <div>
                                <input type="text" placeholder="Full Name " />
                            </div>
                            <div>
                                <input type="email" placeholder="Email" />
                            </div>
                            <div>
                                <input type="text" placeholder="Phone number" />
                            </div>
                            <div>
                                <input type="text" class="message-box" placeholder="Message" />
                            </div>
                            <div class="d-flex">
                                <button class="btn btn-success">SEND</button>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="col-md-7 d-flex justify-content-center">
                    <div class="map_container">
                        <div class="map">
                            <iframe
                                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3961.153515811431!2d107.57158381467832!3d-6.872202069149748!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x0%3A0x7913ceaaa291250e!2zNsKwNTInMTkuMSJTIDEwN8KwMzQnMjUuNSJF!5e0!3m2!1sen!2sid!4v1669960692810!5m2!1sen!2sid"
                                width="600" height="450" style="border:0; z-index: 2;" allowfullscreen=""
                                loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <section class="info_section layout_padding2"
        style="background: url('assets/landing-page/images/bg-bottom.png'); margin-top: -300px;">
        <div class="container">
            <div class="row"
                style="background: white; margin-top: 370px; height: 275px; box-shadow: rgba(102, 187, 106, 0.4) -5px 5px, rgba(102, 187, 106, 0.3) -10px 10px, rgba(102, 187, 106, 0.2) -15px 15px, rgba(102, 187, 106, 0.1) -20px 20px, rgba(102, 187, 106, 0.05) -25px 25px;">
                <div class="col d-flex justify-content-center align-items-center">
                    <div style="margin-top: -40px;">
                        <p style="font-size: 25pt; font-weight: 700;">The Future of <span class="text-success">Farm
                                Technology</span> is Arnesys</p>
                        <div style="display: flex; justify-content: center;">
                            <a href="{{ route('dashboard') }}" class="btn btn-success mt-1" style="width: 250px;">
                                {{ Auth::user() ? 'Dashboard' : 'Login' }}
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="social_container" style="padding-top: 100px;">
                <div class="social_box">
                    <a href="">
                        <i class="bi bi-facebook" aria-hidden="true"></i>
                    </a>
                    <a href="">
                        <i class="bi bi-twitter" aria-hidden="true"></i>
                    </a>
                    <a href="">
                        <i class="bi bi-linkedin" aria-hidden="true"></i>
                    </a>
                    <a href="">
                        <i class="bi bi-instagram" aria-hidden="true"></i>
                    </a>
                    <a href="">
                        <i class="bi bi-youtube" aria-hidden="true"></i>
                    </a>
                </div>
            </div>
            <div class="contact_items">
                <a href="">
                    <div class="item ">
                        <div class="img-box">
                            <i class="bi bi-map-marker" aria-hidden="true"></i>
                        </div>
                        <div class="detail-box">
                            <p>
                                Jurusan Teknik Komputer dan Informatika, POLBAN
                            </p>
                        </div>
                    </div>
                </a>
                <a href="">
                    <div class="item mt-5">
                        <div class="img-box ">
                            <i class="bi bi-phone" aria-hidden="true"></i>
                        </div>
                        <div class="detail-box pl-2">
                            <p>
                                Call +62 83195008217
                            </p>
                        </div>
                    </div>
                </a>
                <a href="">
                    <div class="item mt-5">
                        <div class="img-box ">
                            <i class="bi bi-envelope" aria-hidden="true"></i>
                        </div>
                        <div class="detail-box pl-2">
                            <p>
                                greengrowth.polban@gmail.com
                            </p>
                        </div>
                    </div>
                </a>
            </div>
        </div>
        <div class="info_container">
            <div class="container footer-wrapper">
                <div class="row">
                    <div class="col-md-6 col-lg-3 ">
                        <h6>
                            About
                        </h6>
                        <p>
                            We are from the Greengrowth team, Greengrowth is a team that dreams of becoming a startup, has a
                            main concern in the agricultural sector and is actively looking for solutions to reduce the
                            potential for crop failure as a solution to food security in the world, especially in
                            Indonesia.
                        </p>
                    </div>
                    <div class="col-md-6 col-lg-3 pl-5">
                        <h6>
                            Useful Link
                        </h6>
                        <div class="info_link-box">
                            <ul>
                                <li>
                                    <a href="#technologies-section">
                                        Technologies
                                    </a>
                                </li>
                                <li>
                                    <a href="#about-section">
                                        About
                                    </a>
                                </li>
                                <li>
                                    <a href="#feature-section">
                                        Features
                                    </a>
                                </li>
                                <li>
                                    <a href="#team-section">
                                        Team
                                    </a>
                                </li>
                                <li>
                                    <a href="#contact-section">
                                        Contact
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <div class="col-md-6 col-lg-3 ">
                        <h6>
                            Products
                        </h6>
                        <p>
                            It is an innovative work from a Bandung State Polytechnic student named Arnesys to advance
                            the world of agriculture in Indonesia, especially for the hydroponic method of urban
                            farming.
                        </p>
                    </div>
                    <div class="col-md-6 col-lg-3">
                        <div class="info_form ">
                            <h5>
                                Newsletter
                            </h5>
                            <form action="#">
                                <input type="email" placeholder="Enter your email">
                                <button>
                                    Subscribe
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
                <div class="row mt-3 pl-3">
                    <p class="copyright">© Copyright 2023. greengrowth.co.</p>
                </div>
            </div>
        </div>
    </section>

    <script type="text/javascript" src="{{ asset('assets') }}/landing-page/extensions/jquery/jquery.js"></script>
    <script type="text/javascript" src="{{ asset('assets') }}/landing-page/js/bootstrap.js"></script>
    <script type="text/javascript" src="{{ asset('assets') }}/landing-page/js/custom.js"></script>
    <script type="text/javascript"
        src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCh39n5U-4IoWpsVGUHWdqB6puEkhRLdmI&callback=myMap"></script>

    <script>
        $(".member-item").hover(function() {
            var id = $(this).attr('data-id')
            $(this).css('margin-left', '20px')
            $(`.gallery__item[data-id='${id}']`).find('.gallery__img').css('filter', 'grayscale(0%)')
        }, function() {
            var id = $(this).attr('data-id')
            $(this).css('margin-left', '0px')
            $(`.gallery__item[data-id='${id}']`).find('.gallery__img').css('filter', 'grayscale(90%)')
        })

        $(".gallery__item").hover(function() {
            var id = $(this).attr('data-id')
            $(this).find('.gallery__img').css('filter', 'grayscale(0%)')
            $(`.member-item[data-id='${id}']`).css('margin-left', '20px')
        }, function() {
            var id = $(this).attr('data-id')
            $(this).find('.gallery__img').css('filter', 'grayscale(90%)')
            $(`.member-item[data-id='${id}']`).css('margin-left', '0px')
        })

        window.onscroll = function() {
            scrollFunction()
        }

        function scrollFunction() {
            var reveals = document.querySelectorAll('.reveal')

            if (document.body.scrollTop > 120 || document.documentElement.scrollTop > 120) {
                $("#nav-on-scrolled").css('top', '0px');
            } else {
                $("#nav-on-scrolled").css('top', '-108px');
            }

            for (var i = 0; i < reveals.length; i++) {
                var windowHeight = window.innerHeight
                var revealTop = reveals[i].getBoundingClientRect().top
                var revealPoint = 20

                if (revealTop < windowHeight - revealPoint) {
                    reveals[i].classList.add('active')
                } else {
                    reveals[i].classList.remove('active')
                }
            }
        }
    </script>

</body>

</html>
