begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|MockDirectoryWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CharsRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_comment
comment|/** Tests that&gt; 64k affixes actually works and doesnt overflow some internal int */
end_comment

begin_class
DECL|class|Test64kAffixes
specifier|public
class|class
name|Test64kAffixes
extends|extends
name|LuceneTestCase
block|{
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tempDir
init|=
name|createTempDir
argument_list|(
literal|"64kaffixes"
argument_list|)
decl_stmt|;
name|Path
name|affix
init|=
name|tempDir
operator|.
name|resolve
argument_list|(
literal|"64kaffixes.aff"
argument_list|)
decl_stmt|;
name|Path
name|dict
init|=
name|tempDir
operator|.
name|resolve
argument_list|(
literal|"64kaffixes.dic"
argument_list|)
decl_stmt|;
name|BufferedWriter
name|affixWriter
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|affix
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
comment|// 65k affixes with flag 1, then an affix with flag 2
name|affixWriter
operator|.
name|write
argument_list|(
literal|"SET UTF-8\nFLAG num\nSFX 1 Y 65536\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|65536
condition|;
name|i
operator|++
control|)
block|{
name|affixWriter
operator|.
name|write
argument_list|(
literal|"SFX 1 0 "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|i
argument_list|)
operator|+
literal|" .\n"
argument_list|)
expr_stmt|;
block|}
name|affixWriter
operator|.
name|write
argument_list|(
literal|"SFX 2 Y 1\nSFX 2 0 s\n"
argument_list|)
expr_stmt|;
name|affixWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|BufferedWriter
name|dictWriter
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|dict
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
comment|// drink signed with affix 2 (takes -s)
name|dictWriter
operator|.
name|write
argument_list|(
literal|"1\ndrink/2\n"
argument_list|)
expr_stmt|;
name|dictWriter
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
init|(
name|InputStream
name|affStream
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|affix
argument_list|)
init|;
name|InputStream
name|dictStream
operator|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|dict
argument_list|)
init|;
name|Directory
name|tempDir2
operator|=
name|newDirectory
argument_list|()
init|)
block|{
name|Dictionary
name|dictionary
init|=
operator|new
name|Dictionary
argument_list|(
name|tempDir2
argument_list|,
literal|"dictionary"
argument_list|,
name|affStream
argument_list|,
name|dictStream
argument_list|)
decl_stmt|;
name|Stemmer
name|stemmer
init|=
operator|new
name|Stemmer
argument_list|(
name|dictionary
argument_list|)
decl_stmt|;
comment|// drinks should still stem to drink
name|List
argument_list|<
name|CharsRef
argument_list|>
name|stems
init|=
name|stemmer
operator|.
name|stem
argument_list|(
literal|"drinks"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stems
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"drink"
argument_list|,
name|stems
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

