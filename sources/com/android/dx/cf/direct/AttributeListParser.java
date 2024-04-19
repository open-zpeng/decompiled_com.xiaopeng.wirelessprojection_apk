package com.android.dx.cf.direct;

import com.android.dx.cf.iface.Attribute;
import com.android.dx.cf.iface.ParseException;
import com.android.dx.cf.iface.ParseObserver;
import com.android.dx.cf.iface.StdAttributeList;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class AttributeListParser {
    private final AttributeFactory attributeFactory;
    private final DirectClassFile cf;
    private final int context;
    private int endOffset;
    private final StdAttributeList list;
    private ParseObserver observer;
    private final int offset;

    public AttributeListParser(DirectClassFile directClassFile, int i, int i2, AttributeFactory attributeFactory) {
        Objects.requireNonNull(directClassFile, "cf == null");
        Objects.requireNonNull(attributeFactory, "attributeFactory == null");
        int unsignedShort = directClassFile.getBytes().getUnsignedShort(i2);
        this.cf = directClassFile;
        this.context = i;
        this.offset = i2;
        this.attributeFactory = attributeFactory;
        this.list = new StdAttributeList(unsignedShort);
        this.endOffset = -1;
    }

    public void setObserver(ParseObserver parseObserver) {
        this.observer = parseObserver;
    }

    public int getEndOffset() {
        parseIfNecessary();
        return this.endOffset;
    }

    public StdAttributeList getList() {
        parseIfNecessary();
        return this.list;
    }

    private void parseIfNecessary() {
        if (this.endOffset < 0) {
            parse();
        }
    }

    private void parse() {
        int size = this.list.size();
        int i = this.offset + 2;
        ByteArray bytes = this.cf.getBytes();
        ParseObserver parseObserver = this.observer;
        if (parseObserver != null) {
            parseObserver.parsed(bytes, this.offset, 2, "attributes_count: " + Hex.u2(size));
        }
        for (int i2 = 0; i2 < size; i2++) {
            try {
                ParseObserver parseObserver2 = this.observer;
                if (parseObserver2 != null) {
                    parseObserver2.parsed(bytes, i, 0, "\nattributes[" + i2 + "]:\n");
                    this.observer.changeIndent(1);
                }
                Attribute parse = this.attributeFactory.parse(this.cf, this.context, i, this.observer);
                i += parse.byteLength();
                this.list.set(i2, parse);
                ParseObserver parseObserver3 = this.observer;
                if (parseObserver3 != null) {
                    parseObserver3.changeIndent(-1);
                    this.observer.parsed(bytes, i, 0, "end attributes[" + i2 + "]\n");
                }
            } catch (ParseException e) {
                e.addContext("...while parsing attributes[" + i2 + "]");
                throw e;
            } catch (RuntimeException e2) {
                ParseException parseException = new ParseException(e2);
                parseException.addContext("...while parsing attributes[" + i2 + "]");
                throw parseException;
            }
        }
        this.endOffset = i;
    }
}
