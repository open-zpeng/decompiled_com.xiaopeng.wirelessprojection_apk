package com.android.dx.cf.direct;

import com.android.dx.cf.iface.AttributeList;
import com.android.dx.cf.iface.Member;
import com.android.dx.cf.iface.ParseException;
import com.android.dx.cf.iface.ParseObserver;
import com.android.dx.cf.iface.StdAttributeList;
import com.android.dx.rop.cst.ConstantPool;
import com.android.dx.rop.cst.CstNat;
import com.android.dx.rop.cst.CstString;
import com.android.dx.rop.cst.CstType;
import com.android.dx.util.ByteArray;
import com.android.dx.util.Hex;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class MemberListParser {
    private final AttributeFactory attributeFactory;
    private final DirectClassFile cf;
    private final CstType definer;
    private int endOffset;
    private ParseObserver observer;
    private final int offset;

    protected abstract int getAttributeContext();

    protected abstract String humanAccessFlags(int i);

    protected abstract String humanName();

    protected abstract Member set(int i, int i2, CstNat cstNat, AttributeList attributeList);

    public MemberListParser(DirectClassFile directClassFile, CstType cstType, int i, AttributeFactory attributeFactory) {
        Objects.requireNonNull(directClassFile, "cf == null");
        if (i < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        Objects.requireNonNull(attributeFactory, "attributeFactory == null");
        this.cf = directClassFile;
        this.definer = cstType;
        this.offset = i;
        this.attributeFactory = attributeFactory;
        this.endOffset = -1;
    }

    public int getEndOffset() {
        parseIfNecessary();
        return this.endOffset;
    }

    public final void setObserver(ParseObserver parseObserver) {
        this.observer = parseObserver;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void parseIfNecessary() {
        if (this.endOffset < 0) {
            parse();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final int getCount() {
        return this.cf.getBytes().getUnsignedShort(this.offset);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final CstType getDefiner() {
        return this.definer;
    }

    private void parse() {
        int i;
        ConstantPool constantPool;
        char c;
        int attributeContext = getAttributeContext();
        int count = getCount();
        int i2 = this.offset + 2;
        ByteArray bytes = this.cf.getBytes();
        ConstantPool constantPool2 = this.cf.getConstantPool();
        ParseObserver parseObserver = this.observer;
        if (parseObserver != null) {
            parseObserver.parsed(bytes, this.offset, 2, humanName() + "s_count: " + Hex.u2(count));
        }
        int i3 = 0;
        while (i3 < count) {
            try {
                int unsignedShort = bytes.getUnsignedShort(i2);
                int i4 = i2 + 2;
                int unsignedShort2 = bytes.getUnsignedShort(i4);
                int i5 = i2 + 4;
                int unsignedShort3 = bytes.getUnsignedShort(i5);
                CstString cstString = (CstString) constantPool2.get(unsignedShort2);
                CstString cstString2 = (CstString) constantPool2.get(unsignedShort3);
                ParseObserver parseObserver2 = this.observer;
                int i6 = count;
                if (parseObserver2 != null) {
                    constantPool = constantPool2;
                    parseObserver2.startParsingMember(bytes, i2, cstString.getString(), cstString2.getString());
                    this.observer.parsed(bytes, i2, 0, "\n" + humanName() + "s[" + i3 + "]:\n");
                    this.observer.changeIndent(1);
                    this.observer.parsed(bytes, i2, 2, "access_flags: " + humanAccessFlags(unsignedShort));
                    this.observer.parsed(bytes, i4, 2, "name: " + cstString.toHuman());
                    c = 2;
                    this.observer.parsed(bytes, i5, 2, "descriptor: " + cstString2.toHuman());
                } else {
                    constantPool = constantPool2;
                    c = 2;
                }
                AttributeListParser attributeListParser = new AttributeListParser(this.cf, attributeContext, i2 + 6, this.attributeFactory);
                attributeListParser.setObserver(this.observer);
                i2 = attributeListParser.getEndOffset();
                StdAttributeList list = attributeListParser.getList();
                list.setImmutable();
                Member member = set(i3, unsignedShort, new CstNat(cstString, cstString2), list);
                ParseObserver parseObserver3 = this.observer;
                if (parseObserver3 != null) {
                    parseObserver3.changeIndent(-1);
                    this.observer.parsed(bytes, i2, 0, "end " + humanName() + "s[" + i3 + "]\n");
                    i = i3;
                    try {
                        this.observer.endParsingMember(bytes, i2, cstString.getString(), cstString2.getString(), member);
                    } catch (ParseException e) {
                        e = e;
                        e.addContext("...while parsing " + humanName() + "s[" + i + "]");
                        throw e;
                    } catch (RuntimeException e2) {
                        e = e2;
                        ParseException parseException = new ParseException(e);
                        parseException.addContext("...while parsing " + humanName() + "s[" + i + "]");
                        throw parseException;
                    }
                } else {
                    i = i3;
                }
                i3 = i + 1;
                count = i6;
                constantPool2 = constantPool;
            } catch (ParseException e3) {
                e = e3;
                i = i3;
            } catch (RuntimeException e4) {
                e = e4;
                i = i3;
            }
        }
        this.endOffset = i2;
    }
}
