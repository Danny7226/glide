package com.bumptech.glide.load.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.bitmap_recycle.LruArrayPool;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 18)
public class StreamEncoderTest {
  private StreamEncoder encoder;
  private File file;

  @Before
  public void setUp() {
    encoder = new StreamEncoder(new LruArrayPool());
    file = new File(RuntimeEnvironment.application.getCacheDir(), "test");
  }

  @After
  public void tearDown() {
    // GC before delete() to release files on Windows (https://stackoverflow.com/a/4213208/253468)
    System.gc();
    if (!file.delete()) {
      throw new IllegalStateException("Failed to delete: " + file);
    }
  }

  @Test
  public void testWritesDataFromInputStreamToOutputStream() throws IOException {
    String fakeData = "SomeRandomFakeData";
    ByteArrayInputStream is = new ByteArrayInputStream(fakeData.getBytes("UTF-8"));
    encoder.encode(is, file, new Options());

    byte[] data = ByteBufferUtil.toBytes(ByteBufferUtil.fromFile(file));

    assertEquals(fakeData, new String(data, "UTF-8"));
  }

  @Test
  public void testImportanceOfOptionsArgument() throws IOException {
    String fakeData = "SomeRandomFakeData";
    ByteArrayInputStream is = new ByteArrayInputStream(fakeData.getBytes("UTF-8"));
    boolean success = false;

    success = encoder.encode(is, file, null);

    byte[] data = ByteBufferUtil.toBytes(ByteBufferUtil.fromFile(file));

    assertTrue(success);
    assertEquals(fakeData, new String(data, "UTF-8"));
  }

  @Test(expected = NullPointerException.class)
  public void testFileNullException() throws IOException {
    String fakeData = "SomeRandomFakeData";
    ByteArrayInputStream is = new ByteArrayInputStream(fakeData.getBytes("UTF-8"));
    boolean success = false;
    encoder.encode(is, file, new Options());

    success = encoder.encode(is, null, new Options());

    assertTrue(success);
  }
}
