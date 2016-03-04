begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.internal.csv
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
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
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_comment
comment|/**  * CSVPrinterTest  */
end_comment

begin_class
DECL|class|CSVPrinterTest
specifier|public
class|class
name|CSVPrinterTest
extends|extends
name|TestCase
block|{
DECL|field|lineSeparator
name|String
name|lineSeparator
init|=
literal|"\n"
decl_stmt|;
DECL|method|testPrinter1
specifier|public
name|void
name|testPrinter1
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|CSVPrinter
name|printer
init|=
operator|new
name|CSVPrinter
argument_list|(
name|sw
argument_list|,
name|CSVStrategy
operator|.
name|DEFAULT_STRATEGY
argument_list|)
decl_stmt|;
name|String
index|[]
name|line1
init|=
block|{
literal|"a"
block|,
literal|"b"
block|}
decl_stmt|;
name|printer
operator|.
name|println
argument_list|(
name|line1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a,b"
operator|+
name|lineSeparator
argument_list|,
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrinter2
specifier|public
name|void
name|testPrinter2
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|CSVPrinter
name|printer
init|=
operator|new
name|CSVPrinter
argument_list|(
name|sw
argument_list|,
name|CSVStrategy
operator|.
name|DEFAULT_STRATEGY
argument_list|)
decl_stmt|;
name|String
index|[]
name|line1
init|=
block|{
literal|"a,b"
block|,
literal|"b"
block|}
decl_stmt|;
name|printer
operator|.
name|println
argument_list|(
name|line1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"a,b\",b"
operator|+
name|lineSeparator
argument_list|,
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrinter3
specifier|public
name|void
name|testPrinter3
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|CSVPrinter
name|printer
init|=
operator|new
name|CSVPrinter
argument_list|(
name|sw
argument_list|,
name|CSVStrategy
operator|.
name|DEFAULT_STRATEGY
argument_list|)
decl_stmt|;
name|String
index|[]
name|line1
init|=
block|{
literal|"a, b"
block|,
literal|"b "
block|}
decl_stmt|;
name|printer
operator|.
name|println
argument_list|(
name|line1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"a, b\",\"b \""
operator|+
name|lineSeparator
argument_list|,
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testExcelPrinter1
specifier|public
name|void
name|testExcelPrinter1
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|CSVPrinter
name|printer
init|=
operator|new
name|CSVPrinter
argument_list|(
name|sw
argument_list|,
name|CSVStrategy
operator|.
name|EXCEL_STRATEGY
argument_list|)
decl_stmt|;
name|String
index|[]
name|line1
init|=
block|{
literal|"a"
block|,
literal|"b"
block|}
decl_stmt|;
name|printer
operator|.
name|println
argument_list|(
name|line1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a,b"
operator|+
name|lineSeparator
argument_list|,
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testExcelPrinter2
specifier|public
name|void
name|testExcelPrinter2
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|CSVPrinter
name|printer
init|=
operator|new
name|CSVPrinter
argument_list|(
name|sw
argument_list|,
name|CSVStrategy
operator|.
name|EXCEL_STRATEGY
argument_list|)
decl_stmt|;
name|String
index|[]
name|line1
init|=
block|{
literal|"a,b"
block|,
literal|"b"
block|}
decl_stmt|;
name|printer
operator|.
name|println
argument_list|(
name|line1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"\"a,b\",b"
operator|+
name|lineSeparator
argument_list|,
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|iter
init|=
literal|10000
decl_stmt|;
name|strategy
operator|=
name|CSVStrategy
operator|.
name|DEFAULT_STRATEGY
expr_stmt|;
name|doRandom
argument_list|(
name|iter
argument_list|)
expr_stmt|;
name|strategy
operator|=
name|CSVStrategy
operator|.
name|EXCEL_STRATEGY
expr_stmt|;
name|doRandom
argument_list|(
name|iter
argument_list|)
expr_stmt|;
comment|// Strategy for MySQL
name|strategy
operator|=
operator|new
name|CSVStrategy
argument_list|(
literal|'\t'
argument_list|,
name|CSVStrategy
operator|.
name|ENCAPSULATOR_DISABLED
argument_list|,
name|CSVStrategy
operator|.
name|COMMENTS_DISABLED
argument_list|,
literal|'\\'
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|"\n"
argument_list|)
expr_stmt|;
name|doRandom
argument_list|(
name|iter
argument_list|)
expr_stmt|;
block|}
DECL|field|r
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|strategy
name|CSVStrategy
name|strategy
decl_stmt|;
DECL|method|doRandom
specifier|public
name|void
name|doRandom
parameter_list|(
name|int
name|iter
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|doOneRandom
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|doOneRandom
specifier|public
name|void
name|doOneRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|nLines
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|nCol
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// nLines=1;nCol=2;
name|String
index|[]
index|[]
name|lines
init|=
operator|new
name|String
index|[
name|nLines
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nLines
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|line
init|=
operator|new
name|String
index|[
name|nCol
index|]
decl_stmt|;
name|lines
index|[
name|i
index|]
operator|=
name|line
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nCol
condition|;
name|j
operator|++
control|)
block|{
name|line
index|[
name|j
index|]
operator|=
name|randStr
argument_list|()
expr_stmt|;
block|}
block|}
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|CSVPrinter
name|printer
init|=
operator|new
name|CSVPrinter
argument_list|(
name|sw
argument_list|,
name|strategy
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nLines
condition|;
name|i
operator|++
control|)
block|{
comment|// for (int j=0; j<lines[i].length; j++) System.out.println("### VALUE=:" + printable(lines[i][j]));
name|printer
operator|.
name|println
argument_list|(
name|lines
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|printer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|result
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// System.out.println("### :" + printable(result));
name|StringReader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|CSVParser
name|parser
init|=
operator|new
name|CSVParser
argument_list|(
name|reader
argument_list|,
name|strategy
argument_list|)
decl_stmt|;
name|String
index|[]
index|[]
name|parseResult
init|=
name|parser
operator|.
name|getAllValues
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|equals
argument_list|(
name|lines
argument_list|,
name|parseResult
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Printer output :"
operator|+
name|printable
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|equals
specifier|public
specifier|static
name|boolean
name|equals
parameter_list|(
name|String
index|[]
index|[]
name|a
parameter_list|,
name|String
index|[]
index|[]
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|.
name|length
operator|!=
name|b
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|a
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|linea
init|=
name|a
index|[
name|i
index|]
decl_stmt|;
name|String
index|[]
name|lineb
init|=
name|b
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|linea
operator|.
name|length
operator|!=
name|lineb
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|linea
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|aval
init|=
name|linea
index|[
name|j
index|]
decl_stmt|;
name|String
name|bval
init|=
name|lineb
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|aval
operator|.
name|equals
argument_list|(
name|bval
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"expected  :"
operator|+
name|printable
argument_list|(
name|aval
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"got       :"
operator|+
name|printable
argument_list|(
name|bval
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|printable
specifier|public
specifier|static
name|String
name|printable
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|<=
literal|' '
operator|||
name|ch
operator|>=
literal|128
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|int
operator|)
name|ch
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|randStr
specifier|public
name|String
name|randStr
parameter_list|()
block|{
name|int
name|sz
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
comment|// sz = r.nextInt(3);
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
name|sz
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
comment|// stick in special chars with greater frequency
name|char
name|ch
decl_stmt|;
name|int
name|what
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|what
condition|)
block|{
case|case
literal|0
case|:
name|ch
operator|=
literal|'\r'
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|ch
operator|=
literal|'\n'
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|ch
operator|=
literal|'\t'
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|ch
operator|=
literal|'\f'
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|ch
operator|=
literal|' '
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|ch
operator|=
literal|','
expr_stmt|;
break|break;
case|case
literal|6
case|:
name|ch
operator|=
literal|'"'
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|ch
operator|=
literal|'\''
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|ch
operator|=
literal|'\\'
expr_stmt|;
break|break;
default|default:
name|ch
operator|=
operator|(
name|char
operator|)
name|r
operator|.
name|nextInt
argument_list|(
literal|300
argument_list|)
expr_stmt|;
break|break;
comment|// default: ch = 'a'; break;
block|}
name|buf
index|[
name|i
index|]
operator|=
name|ch
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

