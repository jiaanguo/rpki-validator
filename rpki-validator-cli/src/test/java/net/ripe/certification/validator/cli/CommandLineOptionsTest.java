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
package net.ripe.certification.validator.cli;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.util.Arrays;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class CommandLineOptionsTest {

    private CommandLineOptions subject = new CommandLineOptions();


    @Test(expected=ParseException.class)
    public void shouldFailOnInvalidOption() throws ParseException {
        subject.parse("--invalid");
    }

    @Test
    public void shouldParseHelpShortOption() throws ParseException {
        subject.parse("-h");
        assertTrue(subject.isPrintHelpMode());
    }

    @Test
    public void shouldParseHelpLongOption() throws ParseException {
        subject.parse("--help");
        assertTrue(subject.isPrintHelpMode());
    }

    @Test
    public void shouldParseVersionOption() throws ParseException {
        subject.parse("--version");
        assertTrue(subject.isPrintVersionMode());
    }

    @Test(expected=ParseException.class)
    public void shouldRequireInputFileForPrintOption() throws ParseException {
        subject.parse("-p");
    }

    @Test
    public void shouldParsePrintShortOption() throws ParseException {
        subject.parse("-p", "-f", "file.cer");
        assertTrue(subject.isPrintObjectMode());
    }

    @Test
    public void shouldParsePrintLongOption() throws ParseException {
        subject.parse("--print", "-f", "file.cer");
        assertTrue(subject.isPrintObjectMode());
    }

    @Test
    public void shouldParseInputFileShortOption() throws ParseException {
        subject.parse("-p", "-f", "filename1");
        assertEquals(new File("filename1"), subject.getInputFile());
    }

    @Test
    public void shouldParseInputFileLongOption() throws ParseException {
        subject.parse("-p", "--file", "filename2");
        assertEquals(new File("filename2"), subject.getInputFile());
    }

    @Test
    public void shouldParseTrustAnchorFileShortOption() throws ParseException {
        subject.parse("-t", "filename1", "-o", "/some/where");
        assertNotNull(subject.getTrustAnchorFiles());
        assertTrue(subject.getTrustAnchorFiles().size() == 1);
        assertEquals(new File("filename1"), subject.getTrustAnchorFiles().get(0));
    }

    @Test
    public void shouldParseTrustAnchorFileLongOption() throws ParseException {
        subject.parse("--tal", "filename2", "-o", "/some/where");
        assertNotNull(subject.getTrustAnchorFiles());
        assertTrue(subject.getTrustAnchorFiles().size() == 1);
        assertEquals(new File("filename2"), subject.getTrustAnchorFiles().get(0));
    }

    @Test
    public void shouldParseMultipleTrustAnchorFiles() throws ParseException {
        subject.parse("-t", "filename1", "-t", "filename2", "-o", "/some/where");
        assertNotNull(subject.getTrustAnchorFiles());
        assertTrue(subject.getTrustAnchorFiles().size() == 2);
        assertEquals(new File("filename1"), subject.getTrustAnchorFiles().get(0));
        assertEquals(new File("filename2"), subject.getTrustAnchorFiles().get(1));
    }

    @Test(expected=ParseException.class)
    public void shouldRejectValidationWithoutOutputDirectory() throws ParseException {
        subject.parse("-t", "file.tal");
    }

    @Test
    public void shouldParseOutputDirShortOption() throws ParseException {
        subject.parse("-t", "file.tal", "-o", "dir");
        assertEquals(new File("dir"), subject.getOutputDir());
    }

    @Test
    public void shouldParseOutputDirLongOption() throws ParseException {
        subject.parse("-t", "file.tal", "--output-dir", "dir");
        assertEquals(new File("dir"), subject.getOutputDir());
    }

    @Test
    public void shouldDefaultToEmptyPrefetchUriList() throws ParseException {
        subject.parse("-t", "file.tal", "-o", "dir");
        assertTrue(subject.getPrefetchUris().isEmpty());
    }

    @Test
    public void shouldParsePrefetchURIOption() throws ParseException {
        subject.parse("-t", "file.tal", "--prefetch", "rsync://foo/bar/", "-o", "/some/where");
        assertEquals(Arrays.asList(URI.create("rsync://foo/bar/")), subject.getPrefetchUris());
    }

    @Test
    public void shouldParseMultiplePrefetchURIs() throws ParseException {
        subject.parse("-t", "file.tal", "--prefetch", "rsync://foo/bar/", "--prefetch", "rsync://bar/baz/", "-o", "/some/where");
        assertEquals(Arrays.asList(URI.create("rsync://foo/bar/"), URI.create("rsync://bar/baz/")), subject.getPrefetchUris());
    }

    @Test
    public void shouldIgnoreInvalidPrefetchUri() throws ParseException {
        subject.parse("-t", "file.tal", "--prefetch", "rsync://foo bar/", "-o", "/some/where");
        assertTrue(subject.getPrefetchUris().isEmpty());
    }

    @Test
    public void shouldAppendMissingSlashToPrefetchUri() throws ParseException {
        subject.parse("-t", "file.tal", "--prefetch", "rsync://foo/bar", "-o", "/some/where");
        assertEquals(Arrays.asList(URI.create("rsync://foo/bar/")), subject.getPrefetchUris());
    }

    @Test
    public void shouldDoBottomUpValidationWhenFileOptionUsed() throws ParseException {
        subject.parse("-t", "root.tal", "-f", "file.cer", "-o", "out");
        assertTrue(subject.isValidationMode());
        assertFalse(subject.isTopDownValidationEnabled());
    }

    @Test
    public void shouldDoTopDownValidationWhenFileOptionIsNotUsed() throws ParseException {
        subject.parse("-t", "root.tal", "-o", "out");
        assertTrue(subject.isValidationMode());
        assertTrue(subject.isTopDownValidationEnabled());
    }

    @Test
    public void shouldDefaultToVerboseDisabled() throws ParseException {
        subject.parse("-t", "file.tal", "-o", "dir");
        assertFalse(subject.isVerboseEnabled());
    }

    @Test
    public void shouldParseVerboseShortOption() throws ParseException {
        subject.parse("-t", "file.tal", "-f", "file.cer", "-o", "dir", "-v");
        assertTrue(subject.isVerboseEnabled());
    }

    @Test
    public void shouldParseVerboseLongOption() throws ParseException {
        subject.parse("-t", "file.tal", "-f", "file.cer", "-o", "dir", "--verbose");
        assertTrue(subject.isVerboseEnabled());
    }

    @Test
    public void shouldParseRoaExportShortOption() throws ParseException {
        subject.parse("-t", "file.tal", "-o", "out", "-r", "roa.csv");
        assertTrue(subject.isRoaExportEnabled());
        assertEquals(subject.getRoaExportFile(), new File("roa.csv"));
    }

    @Test
    public void shouldParseRoaExportLongOption() throws ParseException {
        subject.parse("-t", "file.tal", "-o", "out", "--roa-export", "roa.csv");
        assertTrue(subject.isRoaExportEnabled());
        assertEquals(subject.getRoaExportFile(), new File("roa.csv"));
    }
}
