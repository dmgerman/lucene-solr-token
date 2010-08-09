begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.geometry
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geometry
package|;
end_package

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|*
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests for {@link org.apache.lucene.spatial.geometry.DistanceUnits}  */
end_comment

begin_class
DECL|class|TestDistanceUnits
specifier|public
class|class
name|TestDistanceUnits
extends|extends
name|LuceneTestCase
block|{
comment|/**    * Pass condition: When finding the DistanceUnit for "km", KILOMETRES is found.  When finding the DistanceUnit for    * "miles", MILES is found.    */
annotation|@
name|Test
DECL|method|testFindDistanceUnit
specifier|public
name|void
name|testFindDistanceUnit
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|DistanceUnits
operator|.
name|KILOMETERS
argument_list|,
name|DistanceUnits
operator|.
name|findDistanceUnit
argument_list|(
literal|"km"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DistanceUnits
operator|.
name|MILES
argument_list|,
name|DistanceUnits
operator|.
name|findDistanceUnit
argument_list|(
literal|"miles"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Pass condition: Searching for the DistanceUnit of an unknown unit "mls" should throw an IllegalArgumentException.    */
annotation|@
name|Test
DECL|method|testFindDistanceUnit_unknownUnit
specifier|public
name|void
name|testFindDistanceUnit_unknownUnit
parameter_list|()
block|{
try|try
block|{
name|DistanceUnits
operator|.
name|findDistanceUnit
argument_list|(
literal|"mls"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"IllegalArgumentException should have been thrown"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
comment|// Expected
block|}
block|}
comment|/**    * Pass condition: Converting between the same units should not change the value.  Converting from MILES to KILOMETRES    * involves multiplying the distance by the ratio, and converting from KILOMETRES to MILES involves dividing by the ratio    */
annotation|@
name|Test
DECL|method|testConvert
specifier|public
name|void
name|testConvert
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|10.5
argument_list|,
name|DistanceUnits
operator|.
name|MILES
operator|.
name|convert
argument_list|(
literal|10.5
argument_list|,
name|DistanceUnits
operator|.
name|MILES
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.5
argument_list|,
name|DistanceUnits
operator|.
name|KILOMETERS
operator|.
name|convert
argument_list|(
literal|10.5
argument_list|,
name|DistanceUnits
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.5
operator|*
literal|1.609344
argument_list|,
name|DistanceUnits
operator|.
name|KILOMETERS
operator|.
name|convert
argument_list|(
literal|10.5
argument_list|,
name|DistanceUnits
operator|.
name|MILES
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10.5
operator|/
literal|1.609344
argument_list|,
name|DistanceUnits
operator|.
name|MILES
operator|.
name|convert
argument_list|(
literal|10.5
argument_list|,
name|DistanceUnits
operator|.
name|KILOMETERS
argument_list|)
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

