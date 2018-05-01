/*
 * My Digital Safe, the secure notepad Android app.
 * Copyright (C) 2018 Security First Designs
 *
 * My Digital Safe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <a href="www.gnu.org/licenses/">here</a>.
 */

package com.securityfirstdesigns.mydigitalsafe.app;

import java.util.Random;

/**
 * Created by danie on 4/9/2018.
 */

public class UIHelper {
    private String[] Recommendations = {
            "Have you configured your Forgot Password security questions yet?",
            "Enjoying the app? Give us a great rating! We'd appreciate it!",
            "To protect access to your notes, you can setup My Digital Safe to lock your data from excessive failed login attempts.",
            "Remember, 'Quick Notes' from the login page will not be encrypted until the next time you login.",
            "We do not use servers or clouds. We proudly keep all your data on your phone."    };

    public String randomlyGenerateRecommendation() {
        Random rand = new Random();
        int random = rand.nextInt(Recommendations.length);
        return Recommendations[random];
    }
}
