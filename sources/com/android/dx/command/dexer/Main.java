package com.android.dx.command.dexer;

import com.android.dex.Dex;
import com.android.dex.DexException;
import com.android.dex.DexFormat;
import com.android.dex.util.FileUtils;
import com.android.dx.cf.code.SimException;
import com.android.dx.cf.direct.ClassPathOpener;
import com.android.dx.cf.direct.DirectClassFile;
import com.android.dx.cf.direct.StdAttributeFactory;
import com.android.dx.cf.iface.ParseException;
import com.android.dx.command.UsageException;
import com.android.dx.dex.DexOptions;
import com.android.dx.dex.cf.CfOptions;
import com.android.dx.dex.cf.CfTranslator;
import com.android.dx.dex.file.ClassDefItem;
import com.android.dx.dex.file.DexFile;
import com.android.dx.dex.file.EncodedMethod;
import com.android.dx.merge.CollisionPolicy;
import com.android.dx.merge.DexMerger;
import com.android.dx.rop.annotation.Annotation;
import com.android.dx.rop.annotation.Annotations;
import com.android.dx.rop.annotation.AnnotationsList;
import com.android.dx.rop.code.RegisterSpec;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.rop.type.Prototype;
import com.android.dx.rop.type.Type;
import com.android.multidex.ClassPathElement;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
/* loaded from: classes.dex */
public class Main {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String DEX_EXTENSION = ".dex";
    private static final String DEX_PREFIX = "classes";
    private static final String IN_RE_CORE_CLASSES = "Ill-advised or mistaken usage of a core class (java.* or javax.*)\nwhen not building a core library.\n\nThis is often due to inadvertently including a core library file\nin your application's project, when using an IDE (such as\nEclipse). If you are sure you're not intentionally defining a\ncore class, then this is the most likely explanation of what's\ngoing on.\n\nHowever, you might actually be trying to define a class in a core\nnamespace, the source of which you may have taken, for example,\nfrom a non-Android virtual machine project. This will most\nassuredly not work. At a minimum, it jeopardizes the\ncompatibility of your app with future versions of the platform.\nIt is also often of questionable legality.\n\nIf you really intend to build a core library -- which is only\nappropriate as part of creating a full virtual machine\ndistribution, as opposed to compiling an application -- then use\nthe \"--core-library\" option to suppress this error message.\n\nIf you go ahead and use \"--core-library\" but are in fact\nbuilding an application, then be forewarned that your application\nwill still fail to build or run, at some point. Please be\nprepared for angry customers who find, for example, that your\napplication ceases to function once they upgrade their operating\nsystem. You will be to blame for this problem.\n\nIf you are legitimately using some code that happens to be in a\ncore package, then the easiest safe alternative you have is to\nrepackage that code. That is, move the classes in question into\nyour own package namespace. This means that they will never be in\nconflict with core system classes. JarJar is a tool that may help\nyou in this endeavor. If you find that you cannot do this, then\nthat is an indication that the path you are on will ultimately\nlead to pain, suffering, grief, and lamentation.\n";
    private static final String MANIFEST_NAME = "META-INF/MANIFEST.MF";
    private static final int MAX_FIELD_ADDED_DURING_DEX_CREATION = 9;
    private static final int MAX_METHOD_ADDED_DURING_DEX_CREATION = 2;
    private volatile boolean anyFilesProcessed;
    private Arguments args;
    private ExecutorService classDefItemConsumer;
    private ExecutorService classTranslatorPool;
    private final DxContext context;
    private ExecutorService dexOutPool;
    private DexFile outputDex;
    private TreeMap<String, byte[]> outputResources;
    private static final Attributes.Name CREATED_BY = new Attributes.Name("Created-By");
    private static final String[] JAVAX_CORE = {"accessibility", "crypto", "imageio", "management", "naming", "net", "print", "rmi", "security", "sip", "sound", "sql", "swing", "transaction", "xml"};
    private AtomicInteger errors = new AtomicInteger(0);
    private final List<byte[]> libraryDexBuffers = new ArrayList();
    private List<Future<Boolean>> addToDexFutures = new ArrayList();
    private List<Future<byte[]>> dexOutputFutures = new ArrayList();
    private Object dexRotationLock = new Object();
    private int maxMethodIdsInProcess = 0;
    private int maxFieldIdsInProcess = 0;
    private long minimumFileAge = 0;
    private Set<String> classesInMainDex = null;
    private List<byte[]> dexOutputArrays = new ArrayList();
    private OutputStreamWriter humanOutWriter = null;

    public Main(DxContext dxContext) {
        this.context = dxContext;
    }

    public static void main(String[] strArr) throws IOException {
        DxContext dxContext = new DxContext();
        Arguments arguments = new Arguments(dxContext);
        arguments.parse(strArr);
        int runDx = new Main(dxContext).runDx(arguments);
        if (runDx != 0) {
            System.exit(runDx);
        }
    }

    public static void clearInternTables() {
        Prototype.clearInternTable();
        RegisterSpec.clearInternTable();
        CstType.clearInternTable();
        Type.clearInternTable();
    }

    public static int run(Arguments arguments) throws IOException {
        return new Main(new DxContext()).runDx(arguments);
    }

    public int runDx(Arguments arguments) throws IOException {
        OutputStream outputStream;
        this.errors.set(0);
        this.libraryDexBuffers.clear();
        this.args = arguments;
        arguments.makeOptionsObjects();
        if (this.args.humanOutName != null) {
            outputStream = openOutput(this.args.humanOutName);
            this.humanOutWriter = new OutputStreamWriter(outputStream);
        } else {
            outputStream = null;
        }
        try {
            if (this.args.multiDex) {
                return runMultiDex();
            }
            return runMonoDex();
        } finally {
            closeOutput(outputStream);
        }
    }

    private int runMonoDex() throws IOException {
        File file;
        byte[] writeDex;
        if (!this.args.incremental) {
            file = null;
        } else if (this.args.outName == null) {
            this.context.err.println("error: no incremental output name specified");
            return -1;
        } else {
            file = new File(this.args.outName);
            if (file.exists()) {
                this.minimumFileAge = file.lastModified();
            }
        }
        if (processAllFiles()) {
            if (!this.args.incremental || this.anyFilesProcessed) {
                if (this.outputDex.isEmpty() && this.args.humanOutName == null) {
                    writeDex = null;
                } else {
                    writeDex = writeDex(this.outputDex);
                    if (writeDex == null) {
                        return 2;
                    }
                }
                if (this.args.incremental) {
                    writeDex = mergeIncremental(writeDex, file);
                }
                byte[] mergeLibraryDexBuffers = mergeLibraryDexBuffers(writeDex);
                if (this.args.jarOutput) {
                    this.outputDex = null;
                    if (mergeLibraryDexBuffers != null) {
                        this.outputResources.put(DexFormat.DEX_IN_JAR_NAME, mergeLibraryDexBuffers);
                    }
                    if (!createJar(this.args.outName)) {
                        return 3;
                    }
                } else if (mergeLibraryDexBuffers != null && this.args.outName != null) {
                    OutputStream openOutput = openOutput(this.args.outName);
                    openOutput.write(mergeLibraryDexBuffers);
                    closeOutput(openOutput);
                }
                return 0;
            }
            return 0;
        }
        return 1;
    }

    private int runMultiDex() throws IOException {
        if (this.args.mainDexListFile != null) {
            this.classesInMainDex = new HashSet();
            readPathsFromFile(this.args.mainDexListFile, this.classesInMainDex);
        }
        this.dexOutPool = Executors.newFixedThreadPool(this.args.numThreads);
        if (processAllFiles()) {
            if (!this.libraryDexBuffers.isEmpty()) {
                throw new DexException("Library dex files are not supported in multi-dex mode");
            }
            if (this.outputDex != null) {
                this.dexOutputFutures.add(this.dexOutPool.submit(new DexWriter(this.outputDex)));
                this.outputDex = null;
            }
            try {
                this.dexOutPool.shutdown();
                if (!this.dexOutPool.awaitTermination(600L, TimeUnit.SECONDS)) {
                    throw new RuntimeException("Timed out waiting for dex writer threads.");
                }
                for (Future<byte[]> future : this.dexOutputFutures) {
                    this.dexOutputArrays.add(future.get());
                }
                if (this.args.jarOutput) {
                    for (int i = 0; i < this.dexOutputArrays.size(); i++) {
                        this.outputResources.put(getDexFileName(i), this.dexOutputArrays.get(i));
                    }
                    if (!createJar(this.args.outName)) {
                        return 3;
                    }
                } else if (this.args.outName != null) {
                    File file = new File(this.args.outName);
                    for (int i2 = 0; i2 < this.dexOutputArrays.size(); i2++) {
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(file, getDexFileName(i2)));
                        try {
                            fileOutputStream.write(this.dexOutputArrays.get(i2));
                            closeOutput(fileOutputStream);
                        } catch (Throwable th) {
                            closeOutput(fileOutputStream);
                            throw th;
                        }
                    }
                }
                return 0;
            } catch (InterruptedException unused) {
                this.dexOutPool.shutdownNow();
                throw new RuntimeException("A dex writer thread has been interrupted.");
            } catch (Exception unused2) {
                this.dexOutPool.shutdownNow();
                throw new RuntimeException("Unexpected exception in dex writer thread");
            }
        }
        return 1;
    }

    private static String getDexFileName(int i) {
        return i == 0 ? DexFormat.DEX_IN_JAR_NAME : DEX_PREFIX + (i + 1) + DEX_EXTENSION;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void readPathsFromFile(String str, Collection<String> collection) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(str));
            while (true) {
                try {
                    String readLine = bufferedReader2.readLine();
                    if (readLine != null) {
                        collection.add(fixPath(readLine));
                    } else {
                        bufferedReader2.close();
                        return;
                    }
                } catch (Throwable th) {
                    th = th;
                    bufferedReader = bufferedReader2;
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                    throw th;
                }
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private byte[] mergeIncremental(byte[] bArr, File file) throws IOException {
        Dex dex = bArr != null ? new Dex(bArr) : null;
        Dex dex2 = file.exists() ? new Dex(file) : null;
        if (dex == null && dex2 == null) {
            return null;
        }
        if (dex == null) {
            dex = dex2;
        } else if (dex2 != null) {
            dex = new DexMerger(new Dex[]{dex, dex2}, CollisionPolicy.KEEP_FIRST, this.context).merge();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        dex.writeTo(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private byte[] mergeLibraryDexBuffers(byte[] bArr) throws IOException {
        ArrayList arrayList = new ArrayList();
        if (bArr != null) {
            arrayList.add(new Dex(bArr));
        }
        for (byte[] bArr2 : this.libraryDexBuffers) {
            arrayList.add(new Dex(bArr2));
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return new DexMerger((Dex[]) arrayList.toArray(new Dex[arrayList.size()]), CollisionPolicy.FAIL, this.context).merge().getBytes();
    }

    private boolean processAllFiles() {
        createDexFile();
        if (this.args.jarOutput) {
            this.outputResources = new TreeMap<>();
        }
        this.anyFilesProcessed = false;
        String[] strArr = this.args.fileNames;
        Arrays.sort(strArr);
        this.classTranslatorPool = new ThreadPoolExecutor(this.args.numThreads, this.args.numThreads, 0L, TimeUnit.SECONDS, new ArrayBlockingQueue(this.args.numThreads * 2, true), new ThreadPoolExecutor.CallerRunsPolicy());
        this.classDefItemConsumer = Executors.newSingleThreadExecutor();
        try {
            if (this.args.mainDexListFile != null) {
                ClassPathOpener.FileNameFilter mainDexListFilter = this.args.strictNameCheck ? new MainDexListFilter() : new BestEffortMainDexListFilter();
                for (String str : strArr) {
                    processOne(str, mainDexListFilter);
                }
                if (this.dexOutputFutures.size() > 0) {
                    throw new DexException("Too many classes in --main-dex-list, main dex capacity exceeded");
                }
                if (this.args.minimalMainDex) {
                    synchronized (this.dexRotationLock) {
                        while (true) {
                            if (this.maxMethodIdsInProcess <= 0 && this.maxFieldIdsInProcess <= 0) {
                                break;
                            }
                            try {
                                this.dexRotationLock.wait();
                            } catch (InterruptedException unused) {
                            }
                        }
                    }
                    rotateDexFile();
                }
                RemoveModuleInfoFilter removeModuleInfoFilter = new RemoveModuleInfoFilter(new NotFilter(mainDexListFilter));
                for (String str2 : strArr) {
                    processOne(str2, removeModuleInfoFilter);
                }
            } else {
                RemoveModuleInfoFilter removeModuleInfoFilter2 = new RemoveModuleInfoFilter(ClassPathOpener.acceptAll);
                for (String str3 : strArr) {
                    processOne(str3, removeModuleInfoFilter2);
                }
            }
        } catch (StopProcessing unused2) {
        }
        try {
            this.classTranslatorPool.shutdown();
            this.classTranslatorPool.awaitTermination(600L, TimeUnit.SECONDS);
            this.classDefItemConsumer.shutdown();
            this.classDefItemConsumer.awaitTermination(600L, TimeUnit.SECONDS);
            for (Future<Boolean> future : this.addToDexFutures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    if (this.errors.incrementAndGet() < 10) {
                        if (this.args.debug) {
                            this.context.err.println("Uncaught translation error:");
                            e.getCause().printStackTrace(this.context.err);
                        } else {
                            this.context.err.println("Uncaught translation error: " + e.getCause());
                        }
                    } else {
                        throw new InterruptedException("Too many errors");
                    }
                }
            }
            int i = this.errors.get();
            if (i != 0) {
                this.context.err.println(i + " error" + (i == 1 ? "" : "s") + "; aborting");
                return false;
            } else if (!this.args.incremental || this.anyFilesProcessed) {
                if (!this.anyFilesProcessed && !this.args.emptyOk) {
                    this.context.err.println("no classfiles specified");
                    return false;
                }
                if (this.args.optimize && this.args.statistics) {
                    this.context.codeStatistics.dumpStatistics(this.context.out);
                }
                return true;
            } else {
                return true;
            }
        } catch (InterruptedException e2) {
            this.classTranslatorPool.shutdownNow();
            this.classDefItemConsumer.shutdownNow();
            throw new RuntimeException("Translation has been interrupted", e2);
        } catch (Exception e3) {
            this.classTranslatorPool.shutdownNow();
            this.classDefItemConsumer.shutdownNow();
            e3.printStackTrace(this.context.out);
            throw new RuntimeException("Unexpected exception in translator thread.", e3);
        }
    }

    private void createDexFile() {
        this.outputDex = new DexFile(this.args.dexOptions);
        if (this.args.dumpWidth != 0) {
            this.outputDex.setDumpWidth(this.args.dumpWidth);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rotateDexFile() {
        DexFile dexFile = this.outputDex;
        if (dexFile != null) {
            ExecutorService executorService = this.dexOutPool;
            if (executorService != null) {
                this.dexOutputFutures.add(executorService.submit(new DexWriter(this.outputDex)));
            } else {
                this.dexOutputArrays.add(writeDex(dexFile));
            }
        }
        createDexFile();
    }

    private void processOne(String str, ClassPathOpener.FileNameFilter fileNameFilter) {
        if (new ClassPathOpener(str, true, fileNameFilter, new FileBytesConsumer()).process()) {
            updateStatus(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStatus(boolean z) {
        this.anyFilesProcessed = z | this.anyFilesProcessed;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean processFileBytes(String str, long j, byte[] bArr) {
        boolean endsWith = str.endsWith(".class");
        boolean equals = str.equals(DexFormat.DEX_IN_JAR_NAME);
        boolean z = this.outputResources != null;
        if (!endsWith && !equals && !z) {
            if (this.args.verbose) {
                this.context.out.println("ignored resource " + str);
            }
            return false;
        }
        if (this.args.verbose) {
            this.context.out.println("processing " + str + "...");
        }
        String fixPath = fixPath(str);
        if (!endsWith) {
            if (equals) {
                synchronized (this.libraryDexBuffers) {
                    this.libraryDexBuffers.add(bArr);
                }
                return true;
            }
            synchronized (this.outputResources) {
                this.outputResources.put(fixPath, bArr);
            }
            return true;
        }
        if (z && this.args.keepClassesInJar) {
            synchronized (this.outputResources) {
                this.outputResources.put(fixPath, bArr);
            }
        }
        if (j < this.minimumFileAge) {
            return true;
        }
        processClass(fixPath, bArr);
        return false;
    }

    private boolean processClass(String str, byte[] bArr) {
        if (!this.args.coreLibrary) {
            checkClassName(str);
        }
        try {
            new DirectClassFileConsumer(str, bArr, null).call(new ClassParserTask(str, bArr).call());
            return true;
        } catch (ParseException e) {
            throw e;
        } catch (Exception e2) {
            throw new RuntimeException("Exception parsing classes", e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public DirectClassFile parseClass(String str, byte[] bArr) {
        DirectClassFile directClassFile = new DirectClassFile(bArr, str, this.args.cfOptions.strictNameCheck);
        directClassFile.setAttributeFactory(StdAttributeFactory.THE_ONE);
        directClassFile.getMagic();
        return directClassFile;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ClassDefItem translateClass(byte[] bArr, DirectClassFile directClassFile) {
        try {
            return CfTranslator.translate(this.context, directClassFile, bArr, this.args.cfOptions, this.args.dexOptions, this.outputDex);
        } catch (ParseException e) {
            this.context.err.println("\ntrouble processing:");
            if (this.args.debug) {
                e.printStackTrace(this.context.err);
            } else {
                e.printContext(this.context.err);
            }
            this.errors.incrementAndGet();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean addClassToDex(ClassDefItem classDefItem) {
        synchronized (this.outputDex) {
            this.outputDex.add(classDefItem);
        }
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0029, code lost:
        if (java.util.Arrays.binarySearch(com.android.dx.command.dexer.Main.JAVAX_CORE, r6.substring(6, r0)) >= 0) goto L3;
     */
    /* JADX WARN: Removed duplicated region for block: B:14:0x002e A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x002f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void checkClassName(java.lang.String r6) {
        /*
            r5 = this;
            java.lang.String r0 = "java/"
            boolean r0 = r6.startsWith(r0)
            r1 = 0
            r2 = 1
            if (r0 == 0) goto Lc
        La:
            r1 = r2
            goto L2c
        Lc:
            java.lang.String r0 = "javax/"
            boolean r0 = r6.startsWith(r0)
            if (r0 == 0) goto L2c
            r0 = 47
            r3 = 6
            int r0 = r6.indexOf(r0, r3)
            r4 = -1
            if (r0 != r4) goto L1f
            goto La
        L1f:
            java.lang.String r0 = r6.substring(r3, r0)
            java.lang.String[] r3 = com.android.dx.command.dexer.Main.JAVAX_CORE
            int r0 = java.util.Arrays.binarySearch(r3, r0)
            if (r0 < 0) goto L2c
            goto La
        L2c:
            if (r1 != 0) goto L2f
            return
        L2f:
            com.android.dx.command.dexer.DxContext r0 = r5.context
            java.io.PrintStream r0 = r0.err
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "\ntrouble processing \""
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r6 = r1.append(r6)
            java.lang.String r1 = "\":\n\n"
            java.lang.StringBuilder r6 = r6.append(r1)
            java.lang.String r1 = "Ill-advised or mistaken usage of a core class (java.* or javax.*)\nwhen not building a core library.\n\nThis is often due to inadvertently including a core library file\nin your application's project, when using an IDE (such as\nEclipse). If you are sure you're not intentionally defining a\ncore class, then this is the most likely explanation of what's\ngoing on.\n\nHowever, you might actually be trying to define a class in a core\nnamespace, the source of which you may have taken, for example,\nfrom a non-Android virtual machine project. This will most\nassuredly not work. At a minimum, it jeopardizes the\ncompatibility of your app with future versions of the platform.\nIt is also often of questionable legality.\n\nIf you really intend to build a core library -- which is only\nappropriate as part of creating a full virtual machine\ndistribution, as opposed to compiling an application -- then use\nthe \"--core-library\" option to suppress this error message.\n\nIf you go ahead and use \"--core-library\" but are in fact\nbuilding an application, then be forewarned that your application\nwill still fail to build or run, at some point. Please be\nprepared for angry customers who find, for example, that your\napplication ceases to function once they upgrade their operating\nsystem. You will be to blame for this problem.\n\nIf you are legitimately using some code that happens to be in a\ncore package, then the easiest safe alternative you have is to\nrepackage that code. That is, move the classes in question into\nyour own package namespace. This means that they will never be in\nconflict with core system classes. JarJar is a tool that may help\nyou in this endeavor. If you find that you cannot do this, then\nthat is an indication that the path you are on will ultimately\nlead to pain, suffering, grief, and lamentation.\n"
            java.lang.StringBuilder r6 = r6.append(r1)
            java.lang.String r6 = r6.toString()
            r0.println(r6)
            java.util.concurrent.atomic.AtomicInteger r6 = r5.errors
            r6.incrementAndGet()
            com.android.dx.command.dexer.Main$StopProcessing r6 = new com.android.dx.command.dexer.Main$StopProcessing
            r0 = 0
            r6.<init>()
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.dx.command.dexer.Main.checkClassName(java.lang.String):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] writeDex(DexFile dexFile) {
        byte[] dex;
        try {
            if (this.args.methodToDump != null) {
                dexFile.toDex(null, false);
                dumpMethod(dexFile, this.args.methodToDump, this.humanOutWriter);
                dex = null;
            } else {
                dex = dexFile.toDex(this.humanOutWriter, this.args.verboseDump);
            }
            if (this.args.statistics) {
                this.context.out.println(dexFile.getStatistics().toHuman());
            }
            OutputStreamWriter outputStreamWriter = this.humanOutWriter;
            if (outputStreamWriter != null) {
                outputStreamWriter.flush();
            }
            return dex;
        } catch (Exception e) {
            if (this.args.debug) {
                this.context.err.println("\ntrouble writing output:");
                e.printStackTrace(this.context.err);
            } else {
                this.context.err.println("\ntrouble writing output: " + e.getMessage());
            }
            return null;
        }
    }

    private boolean createJar(String str) {
        try {
            Manifest makeManifest = makeManifest();
            OutputStream openOutput = openOutput(str);
            JarOutputStream jarOutputStream = new JarOutputStream(openOutput, makeManifest);
            for (Map.Entry<String, byte[]> entry : this.outputResources.entrySet()) {
                String key = entry.getKey();
                byte[] value = entry.getValue();
                JarEntry jarEntry = new JarEntry(key);
                int length = value.length;
                if (this.args.verbose) {
                    this.context.out.println("writing " + key + "; size " + length + "...");
                }
                jarEntry.setSize(length);
                jarOutputStream.putNextEntry(jarEntry);
                jarOutputStream.write(value);
                jarOutputStream.closeEntry();
            }
            jarOutputStream.finish();
            jarOutputStream.flush();
            closeOutput(openOutput);
            return true;
        } catch (Exception e) {
            if (this.args.debug) {
                this.context.err.println("\ntrouble writing output:");
                e.printStackTrace(this.context.err);
                return false;
            }
            this.context.err.println("\ntrouble writing output: " + e.getMessage());
            return false;
        }
    }

    private Manifest makeManifest() throws IOException {
        Attributes attributes;
        Manifest manifest;
        byte[] bArr = this.outputResources.get(MANIFEST_NAME);
        if (bArr == null) {
            manifest = new Manifest();
            attributes = manifest.getMainAttributes();
            attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        } else {
            Manifest manifest2 = new Manifest(new ByteArrayInputStream(bArr));
            Attributes mainAttributes = manifest2.getMainAttributes();
            this.outputResources.remove(MANIFEST_NAME);
            attributes = mainAttributes;
            manifest = manifest2;
        }
        Attributes.Name name = CREATED_BY;
        String value = attributes.getValue(name);
        attributes.put(name, (value == null ? "" : value + " + ") + "dx 1.16");
        attributes.putValue("Dex-Location", DexFormat.DEX_IN_JAR_NAME);
        return manifest;
    }

    private OutputStream openOutput(String str) throws IOException {
        if (str.equals("-") || str.startsWith("-.")) {
            return this.context.out;
        }
        return new FileOutputStream(str);
    }

    private void closeOutput(OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            return;
        }
        outputStream.flush();
        if (outputStream != this.context.out) {
            outputStream.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String fixPath(String str) {
        if (File.separatorChar == '\\') {
            str = str.replace('\\', ClassPathElement.SEPARATOR_CHAR);
        }
        int lastIndexOf = str.lastIndexOf("/./");
        if (lastIndexOf != -1) {
            return str.substring(lastIndexOf + 3);
        }
        return str.startsWith("./") ? str.substring(2) : str;
    }

    private void dumpMethod(DexFile dexFile, String str, OutputStreamWriter outputStreamWriter) {
        CstString sourceFile;
        boolean endsWith = str.endsWith("*");
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf <= 0 || lastIndexOf == str.length() - 1) {
            this.context.err.println("bogus fully-qualified method name: " + str);
            return;
        }
        String replace = str.substring(0, lastIndexOf).replace('.', ClassPathElement.SEPARATOR_CHAR);
        String substring = str.substring(lastIndexOf + 1);
        ClassDefItem classOrNull = dexFile.getClassOrNull(replace);
        if (classOrNull == null) {
            this.context.err.println("no such class: " + replace);
            return;
        }
        if (endsWith) {
            substring = substring.substring(0, substring.length() - 1);
        }
        ArrayList<EncodedMethod> methods = classOrNull.getMethods();
        TreeMap treeMap = new TreeMap();
        Iterator<EncodedMethod> it = methods.iterator();
        while (it.hasNext()) {
            EncodedMethod next = it.next();
            String string = next.getName().getString();
            if ((endsWith && string.startsWith(substring)) || (!endsWith && string.equals(substring))) {
                treeMap.put(next.getRef().getNat(), next);
            }
        }
        if (treeMap.size() == 0) {
            this.context.err.println("no such method: " + str);
            return;
        }
        PrintWriter printWriter = new PrintWriter(outputStreamWriter);
        for (EncodedMethod encodedMethod : treeMap.values()) {
            encodedMethod.debugPrint(printWriter, this.args.verboseDump);
            if (classOrNull.getSourceFile() != null) {
                printWriter.println("  source file: " + sourceFile.toQuoted());
            }
            Annotations methodAnnotations = classOrNull.getMethodAnnotations(encodedMethod.getRef());
            AnnotationsList parameterAnnotations = classOrNull.getParameterAnnotations(encodedMethod.getRef());
            if (methodAnnotations != null) {
                printWriter.println("  method annotations:");
                Iterator<Annotation> it2 = methodAnnotations.getAnnotations().iterator();
                while (it2.hasNext()) {
                    printWriter.println("    " + it2.next());
                }
            }
            if (parameterAnnotations != null) {
                printWriter.println("  parameter annotations:");
                int size = parameterAnnotations.size();
                for (int i = 0; i < size; i++) {
                    printWriter.println("    parameter " + i);
                    Iterator<Annotation> it3 = parameterAnnotations.get(i).getAnnotations().iterator();
                    while (it3.hasNext()) {
                        printWriter.println("      " + it3.next());
                    }
                }
            }
        }
        printWriter.flush();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class NotFilter implements ClassPathOpener.FileNameFilter {
        private final ClassPathOpener.FileNameFilter filter;

        private NotFilter(ClassPathOpener.FileNameFilter fileNameFilter) {
            this.filter = fileNameFilter;
        }

        @Override // com.android.dx.cf.direct.ClassPathOpener.FileNameFilter
        public boolean accept(String str) {
            return !this.filter.accept(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class RemoveModuleInfoFilter implements ClassPathOpener.FileNameFilter {
        protected final ClassPathOpener.FileNameFilter delegate;

        public RemoveModuleInfoFilter(ClassPathOpener.FileNameFilter fileNameFilter) {
            this.delegate = fileNameFilter;
        }

        @Override // com.android.dx.cf.direct.ClassPathOpener.FileNameFilter
        public boolean accept(String str) {
            return this.delegate.accept(str) && !"module-info.class".equals(str);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MainDexListFilter implements ClassPathOpener.FileNameFilter {
        private MainDexListFilter() {
        }

        @Override // com.android.dx.cf.direct.ClassPathOpener.FileNameFilter
        public boolean accept(String str) {
            if (str.endsWith(".class")) {
                return Main.this.classesInMainDex.contains(Main.fixPath(str));
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BestEffortMainDexListFilter implements ClassPathOpener.FileNameFilter {
        Map<String, List<String>> map = new HashMap();

        public BestEffortMainDexListFilter() {
            for (String str : Main.this.classesInMainDex) {
                String fixPath = Main.fixPath(str);
                String simpleName = getSimpleName(fixPath);
                List<String> list = this.map.get(simpleName);
                if (list == null) {
                    list = new ArrayList<>(1);
                    this.map.put(simpleName, list);
                }
                list.add(fixPath);
            }
        }

        @Override // com.android.dx.cf.direct.ClassPathOpener.FileNameFilter
        public boolean accept(String str) {
            if (str.endsWith(".class")) {
                String fixPath = Main.fixPath(str);
                List<String> list = this.map.get(getSimpleName(fixPath));
                if (list != null) {
                    for (String str2 : list) {
                        if (fixPath.endsWith(str2)) {
                            return true;
                        }
                    }
                    return false;
                }
                return false;
            }
            return true;
        }

        private String getSimpleName(String str) {
            int lastIndexOf = str.lastIndexOf(47);
            return lastIndexOf >= 0 ? str.substring(lastIndexOf + 1) : str;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class StopProcessing extends RuntimeException {
        private StopProcessing() {
        }
    }

    /* loaded from: classes.dex */
    public static class Arguments {
        private static final String INCREMENTAL_OPTION = "--incremental";
        private static final String INPUT_LIST_OPTION = "--input-list";
        private static final String MAIN_DEX_LIST_OPTION = "--main-dex-list";
        private static final String MINIMAL_MAIN_DEX_OPTION = "--minimal-main-dex";
        private static final String MULTI_DEX_OPTION = "--multi-dex";
        private static final String NUM_THREADS_OPTION = "--num-threads";
        public boolean allowAllInterfaceMethodInvokes;
        public CfOptions cfOptions;
        public final DxContext context;
        public boolean coreLibrary;
        public boolean debug;
        public DexOptions dexOptions;
        public String dontOptimizeListFile;
        public int dumpWidth;
        public boolean emptyOk;
        public String[] fileNames;
        public boolean forceJumbo;
        public String humanOutName;
        public boolean incremental;
        private List<String> inputList;
        public boolean jarOutput;
        public boolean keepClassesInJar;
        public boolean localInfo;
        public String mainDexListFile;
        public int maxNumberOfIdxPerDex;
        public String methodToDump;
        public int minSdkVersion;
        public boolean minimalMainDex;
        public boolean multiDex;
        public int numThreads;
        public boolean optimize;
        public String optimizeListFile;
        public String outName;
        private boolean outputIsDirectDex;
        private boolean outputIsDirectory;
        public int positionInfo;
        public boolean statistics;
        public boolean strictNameCheck;
        public boolean verbose;
        public boolean verboseDump;
        public boolean warnings;

        public Arguments(DxContext dxContext) {
            this.debug = false;
            this.warnings = true;
            this.verbose = false;
            this.verboseDump = false;
            this.coreLibrary = false;
            this.methodToDump = null;
            this.dumpWidth = 0;
            this.outName = null;
            this.humanOutName = null;
            this.strictNameCheck = true;
            this.emptyOk = false;
            this.jarOutput = false;
            this.keepClassesInJar = false;
            this.minSdkVersion = 13;
            this.positionInfo = 2;
            this.localInfo = true;
            this.incremental = false;
            this.forceJumbo = false;
            this.allowAllInterfaceMethodInvokes = false;
            this.optimize = true;
            this.optimizeListFile = null;
            this.dontOptimizeListFile = null;
            this.numThreads = 1;
            this.multiDex = false;
            this.mainDexListFile = null;
            this.minimalMainDex = false;
            this.maxNumberOfIdxPerDex = 65536;
            this.inputList = null;
            this.outputIsDirectory = false;
            this.outputIsDirectDex = false;
            this.context = dxContext;
        }

        public Arguments() {
            this(new DxContext());
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class ArgumentsParser {
            private final String[] arguments;
            private String current;
            private int index = 0;
            private String lastValue;

            public ArgumentsParser(String[] strArr) {
                this.arguments = strArr;
            }

            public String getCurrent() {
                return this.current;
            }

            public String getLastValue() {
                return this.lastValue;
            }

            public boolean getNext() {
                int i = this.index;
                String[] strArr = this.arguments;
                if (i >= strArr.length) {
                    return false;
                }
                String str = strArr[i];
                this.current = str;
                if (str.equals("--") || !this.current.startsWith("--")) {
                    return false;
                }
                this.index++;
                return true;
            }

            private boolean getNextValue() {
                int i = this.index;
                String[] strArr = this.arguments;
                if (i >= strArr.length) {
                    return false;
                }
                this.current = strArr[i];
                this.index = i + 1;
                return true;
            }

            public String[] getRemaining() {
                String[] strArr = this.arguments;
                int length = strArr.length;
                int i = this.index;
                int i2 = length - i;
                String[] strArr2 = new String[i2];
                if (i2 > 0) {
                    System.arraycopy(strArr, i, strArr2, 0, i2);
                }
                return strArr2;
            }

            public boolean isArg(String str) {
                int length = str.length();
                if (length > 0) {
                    int i = length - 1;
                    if (str.charAt(i) == '=') {
                        if (this.current.startsWith(str)) {
                            this.lastValue = this.current.substring(length);
                            return true;
                        }
                        String substring = str.substring(0, i);
                        if (this.current.equals(substring)) {
                            if (getNextValue()) {
                                this.lastValue = this.current;
                                return true;
                            }
                            System.err.println("Missing value after parameter " + substring);
                            throw new UsageException();
                        }
                        return false;
                    }
                }
                return this.current.equals(str);
            }
        }

        private void parseFlags(ArgumentsParser argumentsParser) {
            int i;
            while (argumentsParser.getNext()) {
                if (argumentsParser.isArg("--debug")) {
                    this.debug = true;
                } else if (argumentsParser.isArg("--no-warning")) {
                    this.warnings = false;
                } else if (argumentsParser.isArg("--verbose")) {
                    this.verbose = true;
                } else if (argumentsParser.isArg("--verbose-dump")) {
                    this.verboseDump = true;
                } else if (argumentsParser.isArg("--no-files")) {
                    this.emptyOk = true;
                } else if (argumentsParser.isArg("--no-optimize")) {
                    this.optimize = false;
                } else if (argumentsParser.isArg("--no-strict")) {
                    this.strictNameCheck = false;
                } else if (argumentsParser.isArg("--core-library")) {
                    this.coreLibrary = true;
                } else if (argumentsParser.isArg("--statistics")) {
                    this.statistics = true;
                } else if (argumentsParser.isArg("--optimize-list=")) {
                    if (this.dontOptimizeListFile != null) {
                        this.context.err.println("--optimize-list and --no-optimize-list are incompatible.");
                        throw new UsageException();
                    } else {
                        this.optimize = true;
                        this.optimizeListFile = argumentsParser.getLastValue();
                    }
                } else if (argumentsParser.isArg("--no-optimize-list=")) {
                    if (this.dontOptimizeListFile != null) {
                        this.context.err.println("--optimize-list and --no-optimize-list are incompatible.");
                        throw new UsageException();
                    } else {
                        this.optimize = true;
                        this.dontOptimizeListFile = argumentsParser.getLastValue();
                    }
                } else if (argumentsParser.isArg("--keep-classes")) {
                    this.keepClassesInJar = true;
                } else if (argumentsParser.isArg("--output=")) {
                    this.outName = argumentsParser.getLastValue();
                    if (new File(this.outName).isDirectory()) {
                        this.jarOutput = false;
                        this.outputIsDirectory = true;
                    } else if (FileUtils.hasArchiveSuffix(this.outName)) {
                        this.jarOutput = true;
                    } else if (this.outName.endsWith(Main.DEX_EXTENSION) || this.outName.equals("-")) {
                        this.jarOutput = false;
                        this.outputIsDirectDex = true;
                    } else {
                        this.context.err.println("unknown output extension: " + this.outName);
                        throw new UsageException();
                    }
                } else if (argumentsParser.isArg("--dump-to=")) {
                    this.humanOutName = argumentsParser.getLastValue();
                } else if (argumentsParser.isArg("--dump-width=")) {
                    this.dumpWidth = Integer.parseInt(argumentsParser.getLastValue());
                } else if (argumentsParser.isArg("--dump-method=")) {
                    this.methodToDump = argumentsParser.getLastValue();
                    this.jarOutput = false;
                } else if (argumentsParser.isArg("--positions=")) {
                    String intern = argumentsParser.getLastValue().intern();
                    if (intern == "none") {
                        this.positionInfo = 1;
                    } else if (intern == "important") {
                        this.positionInfo = 3;
                    } else if (intern == "lines") {
                        this.positionInfo = 2;
                    } else {
                        this.context.err.println("unknown positions option: " + intern);
                        throw new UsageException();
                    }
                } else if (argumentsParser.isArg("--no-locals")) {
                    this.localInfo = false;
                } else if (argumentsParser.isArg("--num-threads=")) {
                    this.numThreads = Integer.parseInt(argumentsParser.getLastValue());
                } else if (argumentsParser.isArg(INCREMENTAL_OPTION)) {
                    this.incremental = true;
                } else if (argumentsParser.isArg("--force-jumbo")) {
                    this.forceJumbo = true;
                } else if (argumentsParser.isArg(MULTI_DEX_OPTION)) {
                    this.multiDex = true;
                } else if (argumentsParser.isArg("--main-dex-list=")) {
                    this.mainDexListFile = argumentsParser.getLastValue();
                } else if (argumentsParser.isArg(MINIMAL_MAIN_DEX_OPTION)) {
                    this.minimalMainDex = true;
                } else if (argumentsParser.isArg("--set-max-idx-number=")) {
                    this.maxNumberOfIdxPerDex = Integer.parseInt(argumentsParser.getLastValue());
                } else if (argumentsParser.isArg("--input-list=")) {
                    File file = new File(argumentsParser.getLastValue());
                    try {
                        this.inputList = new ArrayList();
                        Main.readPathsFromFile(file.getAbsolutePath(), this.inputList);
                    } catch (IOException unused) {
                        this.context.err.println("Unable to read input list file: " + file.getName());
                        throw new UsageException();
                    }
                } else if (argumentsParser.isArg("--min-sdk-version=")) {
                    String lastValue = argumentsParser.getLastValue();
                    try {
                        i = Integer.parseInt(lastValue);
                    } catch (NumberFormatException unused2) {
                        i = -1;
                    }
                    if (i < 1) {
                        System.err.println("improper min-sdk-version option: " + lastValue);
                        throw new UsageException();
                    }
                    this.minSdkVersion = i;
                } else if (argumentsParser.isArg("--allow-all-interface-method-invokes")) {
                    this.allowAllInterfaceMethodInvokes = true;
                } else {
                    this.context.err.println("unknown option: " + argumentsParser.getCurrent());
                    throw new UsageException();
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void parse(String[] strArr) {
            ArgumentsParser argumentsParser = new ArgumentsParser(strArr);
            parseFlags(argumentsParser);
            this.fileNames = argumentsParser.getRemaining();
            List<String> list = this.inputList;
            if (list != null && !list.isEmpty()) {
                this.inputList.addAll(Arrays.asList(this.fileNames));
                List<String> list2 = this.inputList;
                this.fileNames = (String[]) list2.toArray(new String[list2.size()]);
            }
            if (this.fileNames.length == 0) {
                if (!this.emptyOk) {
                    this.context.err.println("no input files specified");
                    throw new UsageException();
                }
            } else if (this.emptyOk) {
                this.context.out.println("ignoring input files");
            }
            if (this.humanOutName == null && this.methodToDump != null) {
                this.humanOutName = "-";
            }
            String str = this.mainDexListFile;
            if (str != null && !this.multiDex) {
                this.context.err.println("--main-dex-list is only supported in combination with --multi-dex");
                throw new UsageException();
            } else if (this.minimalMainDex && (str == null || !this.multiDex)) {
                this.context.err.println("--minimal-main-dex is only supported in combination with --multi-dex and --main-dex-list");
                throw new UsageException();
            } else {
                boolean z = this.multiDex;
                if (z && this.incremental) {
                    this.context.err.println("--incremental is not supported with --multi-dex");
                    throw new UsageException();
                } else if (z && this.outputIsDirectDex) {
                    this.context.err.println("Unsupported output \"" + this.outName + "\". " + MULTI_DEX_OPTION + " supports only archive or directory output");
                    throw new UsageException();
                } else {
                    if (this.outputIsDirectory && !z) {
                        this.outName = new File(this.outName, DexFormat.DEX_IN_JAR_NAME).getPath();
                    }
                    makeOptionsObjects();
                }
            }
        }

        public void parseFlags(String[] strArr) {
            parseFlags(new ArgumentsParser(strArr));
        }

        public void makeOptionsObjects() {
            CfOptions cfOptions = new CfOptions();
            this.cfOptions = cfOptions;
            cfOptions.positionInfo = this.positionInfo;
            this.cfOptions.localInfo = this.localInfo;
            this.cfOptions.strictNameCheck = this.strictNameCheck;
            this.cfOptions.optimize = this.optimize;
            this.cfOptions.optimizeListFile = this.optimizeListFile;
            this.cfOptions.dontOptimizeListFile = this.dontOptimizeListFile;
            this.cfOptions.statistics = this.statistics;
            if (this.warnings) {
                this.cfOptions.warn = this.context.err;
            } else {
                this.cfOptions.warn = this.context.noop;
            }
            DexOptions dexOptions = new DexOptions(this.context.err);
            this.dexOptions = dexOptions;
            dexOptions.minSdkVersion = this.minSdkVersion;
            this.dexOptions.forceJumbo = this.forceJumbo;
            this.dexOptions.allowAllInterfaceMethodInvokes = this.allowAllInterfaceMethodInvokes;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FileBytesConsumer implements ClassPathOpener.Consumer {
        private FileBytesConsumer() {
        }

        @Override // com.android.dx.cf.direct.ClassPathOpener.Consumer
        public boolean processFileBytes(String str, long j, byte[] bArr) {
            return Main.this.processFileBytes(str, j, bArr);
        }

        @Override // com.android.dx.cf.direct.ClassPathOpener.Consumer
        public void onException(Exception exc) {
            if (exc instanceof StopProcessing) {
                throw ((StopProcessing) exc);
            }
            if (exc instanceof SimException) {
                Main.this.context.err.println("\nEXCEPTION FROM SIMULATION:");
                Main.this.context.err.println(exc.getMessage() + "\n");
                Main.this.context.err.println(((SimException) exc).getContext());
            } else if (exc instanceof ParseException) {
                Main.this.context.err.println("\nPARSE ERROR:");
                ParseException parseException = (ParseException) exc;
                if (Main.this.args.debug) {
                    parseException.printStackTrace(Main.this.context.err);
                } else {
                    parseException.printContext(Main.this.context.err);
                }
            } else {
                Main.this.context.err.println("\nUNEXPECTED TOP-LEVEL EXCEPTION:");
                exc.printStackTrace(Main.this.context.err);
            }
            Main.this.errors.incrementAndGet();
        }

        @Override // com.android.dx.cf.direct.ClassPathOpener.Consumer
        public void onProcessArchiveStart(File file) {
            if (Main.this.args.verbose) {
                Main.this.context.out.println("processing archive " + file + "...");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ClassParserTask implements Callable<DirectClassFile> {
        byte[] bytes;
        String name;

        private ClassParserTask(String str, byte[] bArr) {
            this.name = str;
            this.bytes = bArr;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public DirectClassFile call() throws Exception {
            return Main.this.parseClass(this.name, this.bytes);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DirectClassFileConsumer implements Callable<Boolean> {
        byte[] bytes;
        Future<DirectClassFile> dcff;
        String name;

        private DirectClassFileConsumer(String str, byte[] bArr, Future<DirectClassFile> future) {
            this.name = str;
            this.bytes = bArr;
            this.dcff = future;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public Boolean call() throws Exception {
            return call(this.dcff.get());
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Can't wrap try/catch for region: R(6:23|(2:28|(4:30|31|d2|35)(1:36))|37|38|31|d2) */
        /* JADX WARN: Removed duplicated region for block: B:29:0x00d3 A[Catch: all -> 0x00fa, TRY_ENTER, TryCatch #4 {, blocks: (B:6:0x0030, B:7:0x0036, B:11:0x005d, B:13:0x006e, B:23:0x00ab, B:24:0x00bf, B:15:0x0080, B:17:0x0088, B:20:0x0091, B:22:0x00a5, B:27:0x00cc, B:28:0x00d2, B:26:0x00c3, B:29:0x00d3, B:30:0x00f7, B:8:0x0037, B:9:0x005b), top: B:45:0x0030 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.Boolean call(com.android.dx.cf.direct.DirectClassFile r11) {
            /*
                Method dump skipped, instructions count: 321
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.dx.command.dexer.Main.DirectClassFileConsumer.call(com.android.dx.cf.direct.DirectClassFile):java.lang.Boolean");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ClassTranslatorTask implements Callable<ClassDefItem> {
        byte[] bytes;
        DirectClassFile classFile;
        String name;

        private ClassTranslatorTask(String str, byte[] bArr, DirectClassFile directClassFile) {
            this.name = str;
            this.bytes = bArr;
            this.classFile = directClassFile;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public ClassDefItem call() {
            return Main.this.translateClass(this.bytes, this.classFile);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ClassDefItemConsumer implements Callable<Boolean> {
        Future<ClassDefItem> futureClazz;
        int maxFieldIdsInClass;
        int maxMethodIdsInClass;
        String name;

        private ClassDefItemConsumer(String str, Future<ClassDefItem> future, int i, int i2) {
            this.name = str;
            this.futureClazz = future;
            this.maxMethodIdsInClass = i;
            this.maxFieldIdsInClass = i2;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.concurrent.Callable
        public Boolean call() throws Exception {
            try {
                try {
                    ClassDefItem classDefItem = this.futureClazz.get();
                    if (classDefItem != null) {
                        Main.this.addClassToDex(classDefItem);
                        Main.this.updateStatus(true);
                    }
                    if (Main.this.args.multiDex) {
                        synchronized (Main.this.dexRotationLock) {
                            Main.this.maxMethodIdsInProcess -= this.maxMethodIdsInClass;
                            Main.this.maxFieldIdsInProcess -= this.maxFieldIdsInClass;
                            Main.this.dexRotationLock.notifyAll();
                        }
                    }
                    return true;
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof Exception) {
                        throw ((Exception) cause);
                    }
                    throw e;
                }
            } catch (Throwable th) {
                if (Main.this.args.multiDex) {
                    synchronized (Main.this.dexRotationLock) {
                        Main.this.maxMethodIdsInProcess -= this.maxMethodIdsInClass;
                        Main.this.maxFieldIdsInProcess -= this.maxFieldIdsInClass;
                        Main.this.dexRotationLock.notifyAll();
                    }
                }
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DexWriter implements Callable<byte[]> {
        private final DexFile dexFile;

        private DexWriter(DexFile dexFile) {
            this.dexFile = dexFile;
        }

        @Override // java.util.concurrent.Callable
        public byte[] call() throws IOException {
            return Main.this.writeDex(this.dexFile);
        }
    }
}
