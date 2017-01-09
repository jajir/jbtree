package com.coroptis.jblinktree.performance.tool;

/*
 * #%L
 * jblinktree
 * %%
 * Copyright (C) 2015 coroptis
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class GenerateNumbers {

    private final int[] array;

    private final int base;

    private final Random random;

    GenerateNumbers(final int base, final int length) {
        this.base = base;
        this.array = new int[length];
        this.random = new Random();
        init();
        shuffle();
    }

    private void init() {
        for (int i = 0; i < array.length; i++) {
            array[i] = base + i;
        }
    }

    private void shuffle() {
        final int count = array.length;
        for (int i = count; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    void writeTofile(final String fileName) throws IOException {
        File file = new File(fileName);
        System.out.println(file.getAbsolutePath());
        file.createNewFile();
        BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file)));
        for (int i = 0; i < array.length; i++) {
            bw.write(String.valueOf(array[i]));
            bw.newLine();
        }
        bw.close();
    }

}
