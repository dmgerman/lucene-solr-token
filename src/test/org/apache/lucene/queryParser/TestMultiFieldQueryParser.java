begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
package|;
end_package

begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|standard
operator|.
name|StandardAnalyzer
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
name|search
operator|.
name|Query
import|;
end_import

begin_comment
comment|/**  * Tests QueryParser.  * @author Daniel Naber  */
end_comment

begin_class
DECL|class|TestMultiFieldQueryParser
specifier|public
class|class
name|TestMultiFieldQueryParser
extends|extends
name|TestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|MultiFieldQueryParser
name|mfqp
init|=
operator|new
name|MultiFieldQueryParser
argument_list|(
name|fields
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|Query
name|q
init|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"b:one t:one"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one t:one) (b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"+one +two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one t:one) +(b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"+one -two -three)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one t:one) -(b:two t:two) -(b:three t:three)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one^2 two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"((b:one t:one)^2.0) (b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one~ two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one~0.5 t:one~0.5) (b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one~0.8 two^2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one~0.8 t:one~0.8) ((b:two t:two)^2.0)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one* two*"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one* t:one*) (b:two* t:two*)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"[a TO c] two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:[a TO c] t:[a TO c]) (b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure that terms which have a field are not touched:
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one f:two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one t:one) f:two"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// AND mode:
name|mfqp
operator|.
name|setDefaultOperator
argument_list|(
name|QueryParser
operator|.
name|AND_OPERATOR
argument_list|)
expr_stmt|;
name|q
operator|=
name|mfqp
operator|.
name|parse
argument_list|(
literal|"one two"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one t:one) +(b:two t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// TODO: remove this for Lucene 2.0
DECL|method|testOldMethods
specifier|public
name|void
name|testOldMethods
parameter_list|()
throws|throws
name|ParseException
block|{
comment|// testing the old static calls that are now deprecated:
name|assertQueryEquals
argument_list|(
literal|"b:one t:one"
argument_list|,
literal|"one"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"(b:one b:two) (t:one t:two)"
argument_list|,
literal|"one two"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"(b:one -b:two) (t:one -t:two)"
argument_list|,
literal|"one -two"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"(b:one -(b:two b:three)) (t:one -(t:two t:three))"
argument_list|,
literal|"one -(two three)"
argument_list|)
expr_stmt|;
name|assertQueryEquals
argument_list|(
literal|"(+b:one +b:two) (+t:one +t:two)"
argument_list|,
literal|"+one +two"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: remove this for Lucene 2.0
DECL|method|assertQueryEquals
specifier|private
name|void
name|assertQueryEquals
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|query
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|Query
name|q
init|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|,
name|fields
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|q
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|testStaticMethod1
specifier|public
name|void
name|testStaticMethod1
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|String
index|[]
name|queries
init|=
block|{
literal|"one"
block|,
literal|"two"
block|}
decl_stmt|;
name|Query
name|q
init|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|queries
argument_list|,
name|fields
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"b:one t:two"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries2
init|=
block|{
literal|"+one"
block|,
literal|"+two"
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|queries2
argument_list|,
name|fields
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(+b:one) (+t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries3
init|=
block|{
literal|"one"
block|,
literal|"+two"
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|queries3
argument_list|,
name|fields
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b:one (+t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries4
init|=
block|{
literal|"one +more"
block|,
literal|"+two"
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|queries4
argument_list|,
name|fields
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"(b:one +b:more) (+t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|queries5
init|=
block|{
literal|"blah"
block|}
decl_stmt|;
try|try
block|{
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|queries5
argument_list|,
name|fields
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception, array length differs
block|}
block|}
DECL|method|testStaticMethod2
specifier|public
name|void
name|testStaticMethod2
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|int
index|[]
name|flags
init|=
block|{
name|MultiFieldQueryParser
operator|.
name|REQUIRED_FIELD
block|,
name|MultiFieldQueryParser
operator|.
name|PROHIBITED_FIELD
block|}
decl_stmt|;
name|Query
name|q
init|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
literal|"one"
argument_list|,
name|fields
argument_list|,
name|flags
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"+b:one -t:one"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
literal|"one two"
argument_list|,
name|fields
argument_list|,
name|flags
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+(b:one b:two) -(t:one t:two)"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|int
index|[]
name|flags2
init|=
block|{
name|MultiFieldQueryParser
operator|.
name|REQUIRED_FIELD
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
literal|"blah"
argument_list|,
name|fields
argument_list|,
name|flags2
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception, array length differs
block|}
block|}
DECL|method|testStaticMethod3
specifier|public
name|void
name|testStaticMethod3
parameter_list|()
throws|throws
name|ParseException
block|{
name|String
index|[]
name|queries
init|=
block|{
literal|"one"
block|,
literal|"two"
block|}
decl_stmt|;
name|String
index|[]
name|fields
init|=
block|{
literal|"b"
block|,
literal|"t"
block|}
decl_stmt|;
name|int
index|[]
name|flags
init|=
block|{
name|MultiFieldQueryParser
operator|.
name|REQUIRED_FIELD
block|,
name|MultiFieldQueryParser
operator|.
name|PROHIBITED_FIELD
block|}
decl_stmt|;
name|Query
name|q
init|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|queries
argument_list|,
name|fields
argument_list|,
name|flags
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"+b:one -t:two"
argument_list|,
name|q
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|int
index|[]
name|flags2
init|=
block|{
name|MultiFieldQueryParser
operator|.
name|REQUIRED_FIELD
block|}
decl_stmt|;
name|q
operator|=
name|MultiFieldQueryParser
operator|.
name|parse
argument_list|(
name|queries
argument_list|,
name|fields
argument_list|,
name|flags2
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected exception, array length differs
block|}
block|}
block|}
end_class

end_unit

