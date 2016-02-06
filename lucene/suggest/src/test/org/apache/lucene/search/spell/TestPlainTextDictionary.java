begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/**  * Test case for PlainTextDictionary  *  */
end_comment

begin_class
DECL|class|TestPlainTextDictionary
specifier|public
class|class
name|TestPlainTextDictionary
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBuild
specifier|public
name|void
name|testBuild
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|LF
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
name|String
name|input
init|=
literal|"oneword"
operator|+
name|LF
operator|+
literal|"twoword"
operator|+
name|LF
operator|+
literal|"threeword"
decl_stmt|;
name|PlainTextDictionary
name|ptd
init|=
operator|new
name|PlainTextDictionary
argument_list|(
operator|new
name|StringReader
argument_list|(
name|input
argument_list|)
argument_list|)
decl_stmt|;
name|Directory
name|ramDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|SpellChecker
name|spellChecker
init|=
operator|new
name|SpellChecker
argument_list|(
name|ramDir
argument_list|)
decl_stmt|;
name|spellChecker
operator|.
name|indexDictionary
argument_list|(
name|ptd
argument_list|,
name|newIndexWriterConfig
argument_list|(
literal|null
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
index|[]
name|similar
init|=
name|spellChecker
operator|.
name|suggestSimilar
argument_list|(
literal|"treeword"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|similar
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|0
index|]
argument_list|,
literal|"threeword"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|similar
index|[
literal|1
index|]
argument_list|,
literal|"oneword"
argument_list|)
expr_stmt|;
name|spellChecker
operator|.
name|close
argument_list|()
expr_stmt|;
name|ramDir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

