begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|analysis
operator|.
name|WordlistLoader
import|;
end_import

begin_class
DECL|class|TestWordlistLoader
specifier|public
class|class
name|TestWordlistLoader
extends|extends
name|TestCase
block|{
DECL|method|testWordlistLoading
specifier|public
name|void
name|testWordlistLoading
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|s
init|=
literal|"ONE\n  two \nthree"
decl_stmt|;
name|HashSet
name|wordSet1
init|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
name|checkSet
argument_list|(
name|wordSet1
argument_list|)
expr_stmt|;
name|HashSet
name|wordSet2
init|=
name|WordlistLoader
operator|.
name|getWordSet
argument_list|(
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|checkSet
argument_list|(
name|wordSet2
argument_list|)
expr_stmt|;
block|}
DECL|method|checkSet
specifier|private
name|void
name|checkSet
parameter_list|(
name|HashSet
name|wordset
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|wordset
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|wordset
operator|.
name|contains
argument_list|(
literal|"ONE"
argument_list|)
argument_list|)
expr_stmt|;
comment|// case is not modified
name|assertTrue
argument_list|(
name|wordset
operator|.
name|contains
argument_list|(
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
comment|// surrounding whitespace is removed
name|assertTrue
argument_list|(
name|wordset
operator|.
name|contains
argument_list|(
literal|"three"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|wordset
operator|.
name|contains
argument_list|(
literal|"four"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

