
/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "<WebSig>" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Institute for
 * Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
 * The development of this software was partly funded by the European
 * Commission in the <WebSig> project in the ISIS Programme.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.security.test.c14n.implementations;



import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.test.interop.InteropTest;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.w3c.dom.Element;


/**
 * Interop test for exclusive canonical XML.
 *
 * @author Christian Geuer-Pollmann
 */
public class ExclusiveC14NInterop extends InteropTest {

   /** {@link org.apache.commons.logging} logging facility */
    static org.apache.commons.logging.Log log = 
        org.apache.commons.logging.LogFactory.getLog(ExclusiveC14NInterop.class.getName());

   /**
    * Method suite
    *
    *
    */
   public static Test suite() {
      return new TestSuite(ExclusiveC14NInterop.class);
   }

   /**
    *  Constructor ExclusiveC14NInterop
    *
    *  @param Name_
    */
   public ExclusiveC14NInterop(String Name_) {
      super(Name_);
   }

   /**
     * Setup needed to run test methods individually using JUnit, added by SIR
     */
   public void setUp() {
       org.apache.xml.security.Init.init();
   }
 
   /**
    * Method main
    *
    * @param args
    */
   public static void main(String[] args) {

      String[] testCaseName = { "-noloading",
                                ExclusiveC14NInterop.class.getName() };

      org.apache.xml.security.Init.init();
      junit.textui.TestRunner.main(testCaseName);
   }

   /**
    * Method test_Y1
    *
    * @throws Exception
    */
   public void test_Y1() throws Exception {

      String success = t("data/interop/c14n/Y1", "exc-signature.xml");

      assertTrue(success, success == null);
   }

   /**
    * Method test_Y2
    *
    * @throws Exception
    */
   public void test_Y2() throws Exception {

      String success = t("data/interop/c14n/Y2", "signature-joseph-exc.xml");

      assertTrue(success, success == null);
   }

   /**
    * Method test_Y3
    *
    * @throws Exception
    */
   public void test_Y3() throws Exception {

      String success = t("data/interop/c14n/Y3", "signature.xml");

      assertTrue(success, success == null);
   }

   /**
    * Method test_Y4
    *
    * @throws Exception
    */
   public void test_Y4() throws Exception {

      String success = t("data/interop/c14n/Y4", "signature.xml");

      assertTrue(success, success == null);
   }

   public void test_xfilter2() throws Exception {

      String success = t("data/interop/xfilter2/merlin-xpath-filter2-three", "sign-spec.xml");

      assertTrue(success, success == null);
   }

   /**
    * Method t
    *
    * @param directory
    * @param file
    *
    * @throws Exception
    */
   public String t(String directory, String file) throws Exception {

      File f = new File(directory + "/" + file);
      javax.xml.parsers.DocumentBuilderFactory dbf =
         javax.xml.parsers.DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
      org.w3c.dom.Document doc = db.parse(f);
      long start = System.currentTimeMillis();

      XMLUtils.circumventBug2650(doc);

      long end = System.currentTimeMillis();

      log.debug("fixSubtree took " + (int) (end - start));

      Element sigElement =
         (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                              Constants._TAG_SIGNATURE).item(0);
      XMLSignature signature = new XMLSignature(sigElement,
                                                f.toURL().toString());
      boolean verify =
         signature.checkSignatureValue(signature.getKeyInfo().getPublicKey());

      log.debug("   signature.checkSignatureValue finished: " + verify);

      int failures = 0;

      // if (!verify) {
      if (true) {
         StringBuffer sb = new StringBuffer();

         for (int i = 0; i < signature.getSignedInfo().getLength(); i++) {
            boolean refVerify =
               signature.getSignedInfo().getVerificationResult(i);
            JavaUtils.writeBytesToFilename(directory + "/c14n-" + i + ".apache.html", signature.getSignedInfo().item(i).getHTMLRepresentation().getBytes());

            if (refVerify) {
               log.debug("Reference " + i + " was OK");
            } else {
               failures++;

               sb.append(i + " ");

               JavaUtils.writeBytesToFilename(directory + "/c14n-" + i + ".apache.txt", signature.getSignedInfo().item(i).getContentsAfterTransformation().getBytes());
               JavaUtils.writeBytesToFilename(directory + "/c14n-" + i + ".apache.html", signature.getSignedInfo().item(i).getHTMLRepresentation().getBytes());

               Reference reference = signature.getSignedInfo().item(i);
               int length = reference.getTransforms().getLength();
               String algo = reference.getTransforms().item(length
                  - 1).getURI();

               log.debug("Reference " + i + " failed: " + algo);
            }
         }

         String r = sb.toString().trim();

         if (r.length() == 0) {
            return null;
         } else {
            return r;
         }
      } else {
         return null;
      }
   }
}
