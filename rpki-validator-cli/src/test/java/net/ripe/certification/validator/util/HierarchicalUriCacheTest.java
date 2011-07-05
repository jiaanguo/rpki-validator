/**
 * The BSD License
 *
 * Copyright (c) 2010, 2011 RIPE NCC
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *   - Neither the name of the RIPE NCC nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.ripe.certification.validator.util;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Test;


public class HierarchicalUriCacheTest {

    private HierarchicalUriCache subject = new HierarchicalUriCache();


    @Test
    public void shouldCheckUriAgainstCache() {
        URI uri = URI.create("rsync://host/path/object.roa");
        subject.add(uri);
        assertTrue(subject.contains(uri));
    }

    @Test
    public void shouldContainChildUri() {
        URI parent = URI.create("rsync://host/bar/");
        subject.add(parent);
        assertTrue(subject.contains(URI.create("rsync://host/bar/foo.cer")));
        assertTrue(subject.contains(URI.create("rsync://host/bar/")));
        assertTrue(subject.contains(URI.create("rsync://host/bar/path/")));
    }

    @Test
    public void shouldNotContainDifferentUris() {
        subject.add(URI.create("rsync://host/foo.cer"));
        assertFalse(subject.contains(URI.create("rsync://host/bar.cer")));
        assertFalse(subject.contains(URI.create("rsync://host/foo.cere")));
    }

    @Test
    public void shouldNotContainParentUri() {
        subject.add(URI.create("rsync://host/foo/bar/baz.roa"));
        assertFalse(subject.contains(URI.create("rsync://host/foo/bar/")));
        assertFalse(subject.contains(URI.create("rsync://host/foo/bar")));
        assertFalse(subject.contains(URI.create("rsync://host/foo/bar.cer")));
        assertFalse(subject.contains(URI.create("rsync://host/foo")));
        assertFalse(subject.contains(URI.create("rsync://host/fo")));
        assertFalse(subject.contains(URI.create("rsync://host/")));
        assertFalse(subject.contains(URI.create("rsync://host")));
        assertFalse(subject.contains(URI.create("rsync://host:9999/foo/bar/baz.roa")));
    }

}
