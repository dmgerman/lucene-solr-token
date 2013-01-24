begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"You must increase heap to> 2 G to run this"
argument_list|)
comment|// nocommit: write this test in some other way (not indexinput/output)
DECL|class|Test2BPagedBytes
specifier|public
class|class
name|Test2BPagedBytes
extends|extends
name|LuceneTestCase
block|{
comment|/*   public void test() throws Exception {     PagedBytes pb = new PagedBytes(15);     PagedBytesDataOutput dataOutput = pb.getDataOutput();     long netBytes = 0;     long seed = random().nextLong();     long lastFP = 0;     Random r2 = new Random(seed);     while(netBytes< 1.1*Integer.MAX_VALUE) {       int numBytes = _TestUtil.nextInt(r2, 1, 100000);       byte[] bytes = new byte[numBytes];       r2.nextBytes(bytes);       dataOutput.writeBytes(bytes, bytes.length);       long fp = dataOutput.getPosition();       assert fp == lastFP + numBytes;       lastFP = fp;       netBytes += numBytes;     }     pb.freeze(true);      PagedBytesDataInput dataInput = pb.getDataInput();     lastFP = 0;     r2 = new Random(seed);     netBytes = 0;     while(netBytes< 1.1*Integer.MAX_VALUE) {       int numBytes = _TestUtil.nextInt(r2, 1, 100000);       byte[] bytes = new byte[numBytes];       r2.nextBytes(bytes);        byte[] bytesIn = new byte[numBytes];       dataInput.readBytes(bytesIn, 0, numBytes);       assertTrue(Arrays.equals(bytes, bytesIn));        long fp = dataInput.getPosition();       assert fp == lastFP + numBytes;       lastFP = fp;       netBytes += numBytes;     }   } */
block|}
end_class

end_unit

