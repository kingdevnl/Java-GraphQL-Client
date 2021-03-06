/*
 * MIT License
 *
 * Copyright (c) 2020 KingdevNL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package nl.kingdev.graphqlclient.query;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Getter
@ToString
public class Query {

    private final String query;
    private final JsonObject variables = new JsonObject();

    public Query(String query) {
        this.query = query;
    }

    public static Query fromFile(InputStream file) {
        byte[] bytes;
        try {
            bytes = file.readAllBytes();
            return new Query(new String(bytes, Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Query fromFile(String file) {
        return fromFile(Query.class.getResourceAsStream(file));
    }

    public static Query fromFile(Object owner, String file) {
        return fromFile(owner.getClass().getResourceAsStream(file));
    }

    public Query setVariable(String name, String value) {
        this.variables.addProperty(name, value);
        return this;
    }

    public Query setVariable(String name, JsonObject value) {
        this.variables.add(name, value);
        return this;
    }

    public Query removeVariable(String name) {
        this.variables.remove(name);
        return this;
    }
}
