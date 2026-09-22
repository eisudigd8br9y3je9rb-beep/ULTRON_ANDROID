    private void createUserInterface() {
        setRequestedOrientation(
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        );

        final int AMBER = android.graphics.Color.rgb(235, 135, 45);
        final int GOLD = android.graphics.Color.rgb(244, 190, 120);
        final int TEXT = android.graphics.Color.rgb(190, 174, 155);

        android.widget.FrameLayout root =
                new android.widget.FrameLayout(this);
        root.setBackgroundColor(android.graphics.Color.rgb(3, 10, 16));

        root.addView(
                new com.ultron.assistant.ui.UltronReferenceHudView(this),
                new android.widget.FrameLayout.LayoutParams(-1, -1)
        );

        android.widget.LinearLayout frame =
                new android.widget.LinearLayout(this);
        frame.setOrientation(android.widget.LinearLayout.VERTICAL);
        frame.setPadding(dp(12), dp(5), dp(12), dp(6));
        frame.setAlpha(0f);

        TextView header = new TextView(this);
        header.setText("△  ULTRON");
        header.setTextColor(GOLD);
        header.setTextSize(25);
        header.setGravity(android.view.Gravity.CENTER);
        header.setTypeface(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
        );
        header.setShadowLayer(
                dp(9), 0, 0,
                android.graphics.Color.argb(210, 230, 105, 25)
        );

        frame.addView(
                header,
                new android.widget.LinearLayout.LayoutParams(-1, dp(36))
        );

        TextView subtitle = new TextView(this);
        subtitle.setText("PERSONAL AI COMMAND CENTER");
        subtitle.setTextColor(TEXT);
        subtitle.setTextSize(8);
        subtitle.setGravity(android.view.Gravity.CENTER);

        frame.addView(
                subtitle,
                new android.widget.LinearLayout.LayoutParams(-1, dp(18))
        );

        android.widget.LinearLayout body =
                new android.widget.LinearLayout(this);
        body.setOrientation(android.widget.LinearLayout.HORIZONTAL);

        frame.addView(
                body,
                new android.widget.LinearLayout.LayoutParams(-1, 0, 1f)
        );

        // LEFT COMMAND RAIL
        android.widget.LinearLayout left =
                new android.widget.LinearLayout(this);
        left.setOrientation(android.widget.LinearLayout.VERTICAL);
        left.setPadding(dp(4), dp(4), dp(5), dp(4));
        left.setBackground(makePanel(
                android.graphics.Color.rgb(25, 22, 19),
                android.graphics.Color.rgb(7, 7, 7)
        ));

        body.addView(
                left,
                new android.widget.LinearLayout.LayoutParams(0, -1, 18f)
        );

        TextView navTitle = new TextView(this);
        navTitle.setText("COMMAND");
        navTitle.setTextColor(AMBER);
        navTitle.setTextSize(8);
        navTitle.setPadding(dp(5), dp(2), dp(5), dp(4));
        left.addView(navTitle);

        Button homeButton = new Button(this);
        homeButton.setText("⌂   HOME");

        Button chatNav = new Button(this);
        chatNav.setText("●   CHAT");

        Button createNav = new Button(this);
        createNav.setText("✦   CREATE");

        Button analyseNav = new Button(this);
        analyseNav.setText("▥   ANALYSE");

        Button settingsButton = new Button(this);
        settingsButton.setText("⚙   SETTINGS");

        Button helpNav = new Button(this);
        helpNav.setText("?   HELP");

        Button[] navButtons = {
                homeButton, chatNav, createNav,
                analyseNav, settingsButton, helpNav
        };

        for (Button b : navButtons) {
            styleHudButton(b);
            android.widget.LinearLayout.LayoutParams lp =
                    new android.widget.LinearLayout.LayoutParams(
                            -1, dp(34)
                    );
            lp.setMargins(0, dp(2), 0, dp(2));
            left.addView(b, lp);
        }

        TextView profile = new TextView(this);
        profile.setText(
                "\nULTRON\n" +
                "ONLINE\n\n" +
                "SYSTEM STATUS\n" +
                "CPU       23%\n" +
                "MEMORY    45%\n" +
                "NETWORK   ONLINE"
        );
        profile.setTextColor(TEXT);
        profile.setTextSize(7);
        profile.setPadding(dp(7), dp(4), dp(4), dp(4));
        left.addView(
                profile,
                new android.widget.LinearLayout.LayoutParams(-1, 0, 1f)
        );

        // CENTER CORE
        android.widget.LinearLayout center =
                new android.widget.LinearLayout(this);
        center.setOrientation(android.widget.LinearLayout.VERTICAL);
        center.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
        center.setPadding(dp(7), 0, dp(7), 0);

        body.addView(
                center,
                new android.widget.LinearLayout.LayoutParams(0, -1, 58f)
        );

        TextView conversation = new TextView(this);
        conversation.setText("＋   NEW CONVERSATION");
        conversation.setTextColor(TEXT);
        conversation.setTextSize(8);
        conversation.setGravity(android.view.Gravity.CENTER);

        center.addView(
                conversation,
                new android.widget.LinearLayout.LayoutParams(-1, dp(22))
        );

        ultronCoreView = new com.ultron.assistant.ui.UltronCoreView(this);
        ultronCoreView.setMode(0);

        center.addView(
                ultronCoreView,
                new android.widget.LinearLayout.LayoutParams(-1, 0, 1f)
        );

        TextView coreLabel = new TextView(this);
        coreLabel.setText("AI CORE  •  STANDBY");
        coreLabel.setTextColor(AMBER);
        coreLabel.setTextSize(9);
        coreLabel.setGravity(android.view.Gravity.CENTER);

        center.addView(
                coreLabel,
                new android.widget.LinearLayout.LayoutParams(-1, dp(19))
        );

        android.widget.LinearLayout tools =
                new android.widget.LinearLayout(this);
        tools.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        tools.setGravity(android.view.Gravity.CENTER);

        String[] names = {
                "VOICE", "GENERATE", "RESEARCH",
                "CHAT", "SUMMARIZE", "ANALYSE",
                "OPTIMIZE", "IDEATE"
        };

        for (String name : names) {
            Button b = new Button(this);
            b.setText("◉\n" + name);
            styleHudButton(b);
            b.setTextSize(6);

            if ("VOICE".equals(name)) {
                b.setOnClickListener(v -> startVoice());
            }

            android.widget.LinearLayout.LayoutParams lp =
                    new android.widget.LinearLayout.LayoutParams(
                            0, dp(39), 1f
                    );
            lp.setMargins(dp(1), 0, dp(1), 0);
            tools.addView(b, lp);
        }

        center.addView(
                tools,
                new android.widget.LinearLayout.LayoutParams(-1, dp(41))
        );

        android.widget.LinearLayout command =
                new android.widget.LinearLayout(this);
        command.setOrientation(android.widget.LinearLayout.HORIZONTAL);

        TextView mode = new TextView(this);
        mode.setText("◎ STANDARD");
        mode.setTextColor(TEXT);
        mode.setTextSize(7);
        mode.setGravity(android.view.Gravity.CENTER);
        mode.setBackground(makePanel(
                android.graphics.Color.rgb(30, 26, 22),
                android.graphics.Color.rgb(8, 8, 8)
        ));

        command.addView(
                mode,
                new android.widget.LinearLayout.LayoutParams(
                        dp(78), dp(31)
                )
        );

        TextView ask = new TextView(this);
        ask.setText("   Ask anything...");
        ask.setTextColor(
                android.graphics.Color.rgb(130, 120, 110)
        );
        ask.setTextSize(9);
        ask.setGravity(android.view.Gravity.CENTER_VERTICAL);
        ask.setBackground(makePanel(
                android.graphics.Color.rgb(27, 24, 21),
                android.graphics.Color.rgb(7, 7, 7)
        ));

        android.widget.LinearLayout.LayoutParams askLp =
                new android.widget.LinearLayout.LayoutParams(
                        0, dp(31), 1f
                );
        askLp.setMargins(dp(4), 0, 0, 0);
        command.addView(ask, askLp);

        center.addView(
                command,
                new android.widget.LinearLayout.LayoutParams(-1, dp(35))
        );

        // RIGHT STATUS DECK
        android.widget.LinearLayout right =
                new android.widget.LinearLayout(this);
        right.setOrientation(android.widget.LinearLayout.VERTICAL);
        right.setPadding(dp(5), dp(4), dp(4), dp(4));

        body.addView(
                right,
                new android.widget.LinearLayout.LayoutParams(0, -1, 24f)
        );

        TextView metrics = new TextView(this);
        metrics.setText(
                "SYSTEM MONITOR\n\n" +
                "CPU       ▰▰▰▱  23%\n" +
                "MEMORY    ▰▰▱▱  45%\n" +
                "NETWORK   ▰▰▰▱  ONLINE\n" +
                "SECURITY  ✓  ACTIVE"
        );
        metrics.setTextColor(TEXT);
        metrics.setTextSize(7);
        metrics.setPadding(dp(8), dp(5), dp(5), dp(5));
        metrics.setBackground(makePanel(
                android.graphics.Color.rgb(27, 24, 21),
                android.graphics.Color.rgb(7, 7, 7)
        ));

        right.addView(
                metrics,
                new android.widget.LinearLayout.LayoutParams(-1, dp(78))
        );

        status = new TextView(this);
        status.setText("●  ULTRON ONLINE");
        status.setTextColor(GOLD);
        status.setTextSize(10);
        status.setGravity(android.view.Gravity.CENTER);
        status.setBackground(makePanel(
                android.graphics.Color.rgb(28, 24, 20),
                android.graphics.Color.rgb(7, 7, 7)
        ));

        android.widget.LinearLayout.LayoutParams statusLp =
                new android.widget.LinearLayout.LayoutParams(-1, 0, 1f);
        statusLp.setMargins(0, dp(5), 0, dp(5));
        right.addView(status, statusLp);

        preview = new TextureView(this);

        right.addView(
                preview,
                new android.widget.LinearLayout.LayoutParams(-1, dp(82))
        );

        TextView insights = new TextView(this);
        insights.setText(
                "INSIGHTS\n\n" +
                "• AI CORE READY\n" +
                "• VISION LINK READY\n" +
                "• VOICE LINK READY\n" +
                "• SYSTEM ONLINE"
        );
        insights.setTextColor(TEXT);
        insights.setTextSize(7);
        insights.setPadding(dp(8), dp(5), dp(5), dp(5));
        insights.setBackground(makePanel(
                android.graphics.Color.rgb(27, 24, 21),
                android.graphics.Color.rgb(7, 7, 7)
        ));

        right.addView(
                insights,
                new android.widget.LinearLayout.LayoutParams(-1, dp(80))
        );

        // ACTION DECK
        android.widget.LinearLayout actions =
                new android.widget.LinearLayout(this);
        actions.setOrientation(android.widget.LinearLayout.HORIZONTAL);

        Button cameraLiveButton = new Button(this);
        cameraLiveButton.setText("CAMERA");

        Button frontCameraButton = new Button(this);
        frontCameraButton.setText("FRONT");

        Button rearCameraButton = new Button(this);
        rearCameraButton.setText("REAR");

        Button voiceButton = new Button(this);
        voiceButton.setText("VOICE");

        Button youtubeButton = new Button(this);
        youtubeButton.setText("YOUTUBE");

        Button flashlightOnButton = new Button(this);
        flashlightOnButton.setText("LIGHT");

        Button flashlightOffButton = new Button(this);
        flashlightOffButton.setText("LIGHT OFF");

        Button sleep = new Button(this);
        sleep.setText("SLEEP");

        Button activate = new Button(this);
        activate.setText("ACTIVATE");

        Button[] actionButtons = {
                cameraLiveButton, frontCameraButton, rearCameraButton,
                voiceButton, youtubeButton, flashlightOnButton,
                flashlightOffButton, sleep, activate
        };

        for (Button b : actionButtons) {
            styleHudButton(b);
            b.setTextSize(6);

            android.widget.LinearLayout.LayoutParams lp =
                    new android.widget.LinearLayout.LayoutParams(
                            0, dp(31), 1f
                    );
            lp.setMargins(dp(1), 0, dp(1), 0);
            actions.addView(b, lp);
        }

        frame.addView(
                actions,
                new android.widget.LinearLayout.LayoutParams(-1, dp(35))
        );

        root.addView(
                frame,
                new android.widget.FrameLayout.LayoutParams(-1, -1)
        );

        setContentView(root);
        dashboardHandler.post(dashboardUpdater);

        activate.setOnClickListener(v -> {
            ultronActive = true;
            ultronWaiting = false;
            status.setText("● ULTRON ACTIVE");

            try {
                Intent serviceIntent =
                        new Intent(this, UltronBackgroundService.class);

                if (android.os.Build.VERSION.SDK_INT >=
                        android.os.Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            startVoice();
        });

        sleep.setOnClickListener(v -> {
            ultronActive = false;
            ultronWaiting = true;

            if (ultronCoreView != null) {
                ultronCoreView.sleep();
            }

            if (voiceManager != null) {
                voiceManager.stopListening();
            }

            if (voiceSpeaker != null) {
                voiceSpeaker.stop();
            }

            status.setText("● ULTRON SLEEPING");
        });

        cameraLiveButton.setOnClickListener(v -> {
            if (camera != null) {
                closeCamera();
                status.setText("● CAMERA LIVE: OFF");
                cameraLiveButton.setText("CAMERA");
                return;
            }

            if (checkSelfPermission(
                    Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST
                );
                status.setText("Camera permission required");
                return;
            }

            status.setText("● CAMERA LIVE: STARTING...");
            cameraLiveButton.setText("CAMERA ON");
            openCamera(true);
        });

        frontCameraButton.setOnClickListener(v -> {
            if (checkSelfPermission(
                    Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST
                );
                status.setText("Camera permission required");
                return;
            }

            status.setText("● FRONT CAMERA: STARTING...");
            openCamera(false);
        });

        rearCameraButton.setOnClickListener(v -> {
            if (checkSelfPermission(
                    Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST
                );
                status.setText("Camera permission required");
                return;
            }

            status.setText("● REAR CAMERA: STARTING...");
            openCamera(true);
        });

        voiceButton.setOnClickListener(v -> startVoice());
        youtubeButton.setOnClickListener(v -> openYouTube());
        settingsButton.setOnClickListener(v -> openSettings());
        homeButton.setOnClickListener(v -> goHome());
        flashlightOnButton.setOnClickListener(v -> setFlashlight(true));
        flashlightOffButton.setOnClickListener(v -> setFlashlight(false));
    }


