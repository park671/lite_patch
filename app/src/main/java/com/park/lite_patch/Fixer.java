package com.park.lite_patch;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

public class Fixer {

    private static final String TAG = "LitePatch";

    private static native long getArtMethodSize(Method pre, Method next);

    private static native boolean testInBuiltArtMethod(Method a);

    private static native void replace(Method src, Method dest);

    private static volatile boolean fixerAvailable = false;

    public static boolean isFixerAvailable() {
        return fixerAvailable;
    }

    public static void setup(Context context) {
        try {
            File soFile = writeSoFile(context);
            if (soFile == null) {
                Log.e(TAG, "can not write so file.");
                return;
            }
            System.load(soFile.getAbsolutePath());
            Method pre = MethodStub.class.getDeclaredMethod("func1");
            Method next = MethodStub.class.getDeclaredMethod("func2");
            Log.d(TAG, "art method size=" + getArtMethodSize(pre, next));
            testInBuiltArtMethod(pre);
            fixerAvailable = true;
            Log.i(TAG, "[+] fixer is ready!");
        } catch (Throwable tr) {
            Log.e(TAG, "[-] getArtMethodSize fail", tr);
        }
    }

    public static void fix(Method needFixMethod, Method fixedMethod) {
        if (!fixerAvailable) {
            Log.w(TAG, "[-] not available");
            return;
        }
        replace(needFixMethod, fixedMethod);
    }

    private static File writeSoFile(Context context) {
        byte[] bytes = Base64.decode(nativeCodes, Base64.NO_WRAP);
        File soFileDir = new File(context.getDataDir(), "lib/arm64");
        soFileDir.mkdirs();
        File soFile = new File(soFileDir, "liblite_patch.so");
        if (soFile.exists()) {
            soFile.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(soFile);
            fos.write(bytes);
            fos.flush();
            fos.close();
            soFile.setReadOnly();
            return soFile;
        } catch (Throwable tr) {
            Log.e(TAG, "writeSoFile fail.", tr);
        }
        return null;
    }

    private static class MethodStub {
        public void func1() {
        }
        public void func2() {
        }
    }

    private static final String nativeCodes = "f0VMRgIBAQAAAAAAAAAAAAMAtwABAAAAAAAAAAAAAABAAAAAAAAAAKgXAAAAAAAAAAAAAEAAOAAJAEAAFgAVAAYAAAAEAAAAQAAAAAAAAABAAAAAAAAAAEAAAAAAAAAA+AEAAAAAAAD4AQAAAAAAAAgAAAAAAAAAAQAAAAUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADwEwAAAAAAAPATAAAAAAAAABAAAAAAAAABAAAABgAAAPATAAAAAAAA8CMAAAAAAADwIwAAAAAAACgCAAAAAAAAKAIAAAAAAAAAEAAAAAAAAAEAAAAGAAAAGBYAAAAAAAAYNgAAAAAAABg2AAAAAAAAAAAAAAAAAAAIAAAAAAAAAAAQAAAAAAAAAgAAAAYAAAAIFAAAAAAAAAgkAAAAAAAACCQAAAAAAACwAQAAAAAAALABAAAAAAAACAAAAAAAAABS5XRkBAAAAPATAAAAAAAA8CMAAAAAAADwIwAAAAAAACgCAAAAAAAAEAwAAAAAAAABAAAAAAAAAFDldGQEAAAAOAkAAAAAAAA4CQAAAAAAADgJAAAAAAAAnAAAAAAAAACcAAAAAAAAAAQAAAAAAAAAUeV0ZAYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAABAAAADgCAAAAAAAAOAIAAAAAAAA4AgAAAAAAALwAAAAAAAAAvAAAAAAAAAAEAAAAAAAAAAgAAACEAAAAAQAAAEFuZHJvaWQAHAAAAHIyNWIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA4OTM3MzkzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAABQAAAADAAAAR05VAMGO+CtMTghO7KUuaSsZHS5KynixAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAASAAAAAAAAAAAAAAAAAAAAAAAAABAAAAASAAAAAAAAAAAAAAAAAAAAAAAAAB0AAAASAAAAAAAAAAAAAAAAAAAAAAAAAGIAAAASAAAAAAAAAAAAAAAAAAAAAAAAAPUAAAASAAAAAAAAAAAAAAAAAAAAAAAAAC8AAAASAA0AZAwAAAAAAAAEAgAAAAAAAJ0AAAAiAA0A8BEAAAAAAABEAAAAAAAAAAIBAAAiAA0AiBEAAAAAAAA0AAAAAAAAAC0BAAASAA0AcBIAAAAAAABAAAAAAAAAAF4BAAASAA0AsBIAAAAAAABQAAAAAAAAAHYAAAAiAA0AvBEAAAAAAAA0AAAAAAAAAMUAAAAiAA0ANBIAAAAAAAA8AAAAAAAAAJMBAAASAA0AABMAAAAAAAA4AAAAAAAAAAAAAgACAAIAAQACAAEAAQABAAEAAQABAAEAAQABAAEAuwEAABAAAAAAAAAAYw0FAAAAAgDDAQAAAAAAAAAAAAACAAAABgAAAAIAAAAaAAAAAAAAAAAAAUAATYEgADAoBAYAAAALAAAA0PayLOxgorZIaSTUvl5UwvsfQXZK2mQ5cnhrKdfRIDoAX19jeGFfZmluYWxpemUAX19jeGFfYXRleGl0AF9fcmVnaXN0ZXJfYXRmb3JrAF9aMjdyZWFsVGVzdEFjY2Vzc0ZsYWdBdmFpbGFibGVQN19KTklFbnZQOF9qb2JqZWN0AF9fYW5kcm9pZF9sb2dfcHJpbnQAX1pON19KTklFbnYxNEdldE9iamVjdENsYXNzRVA4X2pvYmplY3QAX1pON19KTklFbnYxMEdldEZpZWxkSURFUDdfamNsYXNzUEtjUzNfAF9aTjdfSk5JRW52MTJHZXRMb25nRmllbGRFUDhfam9iamVjdFA5X2pmaWVsZElEAF9fbWVtY3B5X2NoawBfWk43X0pOSUVudjE4RnJvbVJlZmxlY3RlZEZpZWxkRVA4X2pvYmplY3QASmF2YV9jb21fcGFya19saXRlXzFwYXRjaF9GaXhlcl9nZXRBcnRNZXRob2RTaXplAEphdmFfY29tX3BhcmtfbGl0ZV8xcGF0Y2hfRml4ZXJfdGVzdEluQnVpbHRBcnRNZXRob2QASmF2YV9jb21fcGFya19saXRlXzFwYXRjaF9GaXhlcl9yZXBsYWNlAGxpYmMuc28ATElCQwBsaWJhbmRyb2lkLnNvAGxpYmxvZy5zbwBsaWJtLnNvAGxpYmRsLnNvAGxpYmxpdGVfcGF0Y2guc28AAAAAAAAA8CMAAAAAAAADBAAAAAAAAPAjAAAAAAAA+CMAAAAAAAADBAAAAAAAABwMAAAAAAAA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";

}
