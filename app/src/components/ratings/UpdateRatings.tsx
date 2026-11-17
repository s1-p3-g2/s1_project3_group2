import {ActivityIndicator, Button, FlatList, StyleSheet, View} from "react-native";
import {putRating, RatingResponse} from "@/services/ratings-api";
import {useState} from "react";
import {ThemedView} from "@/components/themed-view";
import {ThemedText} from "@/components/themed-text";
import {BottomTabInset, MaxContentWidth, Spacing} from "@/constants/theme";
import {TextInput, useNativeState} from "@expo/ui";
import {RadioButton} from "@expo/ui/jetpack-compose";
import {RadioGroup} from "@/components/CustomRadioSelect";

export default function UpdateRatingsModule() {
  const userId = useNativeState('');
  const duckId = useNativeState('');
  const [ratingChoice, setRatingChoice] = useState<boolean>(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [rating, setRating] = useState<RatingResponse>();

  type RatingChoice = 'true' | 'false';
  const selectedRating: RatingChoice = ratingChoice ? "true" : "false";

  /**
   * Effect callback function
   * Sets useStates defined above and calls api service method
   */
  async function updateRating() {
    try {
      setLoading(true);
      setError(null);

      var duckIdNum = parseInt(duckId.get(), 10);
      if (isNaN(duckIdNum) || duckId.get().length == 0) {
        throw Error("Duck Id is not valid: Not a number or blank")
      }
      var userIdNum = parseInt(userId.get(), 10);
      if (isNaN(userIdNum) || duckId.get().length == 0) {
        throw Error("User Id is not valid: Not a number or blank")
      }

      const result = await putRating(userIdNum, duckIdNum, ratingChoice);
      setRating(result)
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message)
      } else {
        setError("Unknown error")
      }
    } finally {
      setLoading(false)
    }
  }

  return (
      <ThemedView style={styles.safeArea}>
        <ThemedText type="subtitle">Update Duck Ratings</ThemedText>
        <Button
            title={"Update Rating"}
            onPress={updateRating}
            disabled={loading}
        />

        <ThemedText>
          User Id
        </ThemedText>
        <TextInput
            // style={styles.input}
            placeholder="eg. 1"
            value={userId}
        />

        <ThemedText>
          Duck Id
        </ThemedText>
        <TextInput
            placeholder="eg. 2"
            value={duckId}
        />

        <ThemedText>
          Like/Dislike
        </ThemedText>
        <ThemedView style={styles.radioSection}>
          <RadioGroup<RatingChoice>
              options={[
                {label: "Positive", value: "true"},
                {label: "Negative", value: "false"},
              ]}
              selectedValue={selectedRating}
              onSelect={(value) => setRatingChoice(value === 'true')}
          />

          {loading && <ActivityIndicator/>}

          {error && <ThemedText themeColor="textSecondary">Error: {error}</ThemedText>}

          {!loading && !error && (
              <>
                {rating && ratingRow(rating)}
              </>
          )}
      </ThemedView>

      </ThemedView>
  );
}

function ratingRow(item: RatingResponse) {
  return (
      <ThemedView>
        <ThemedText>
          user_id:{item.userId}, duck_id:{item.duckId}, isPositive:{item.isPositive ? 'true' : 'false'}
        </ThemedText>
      </ThemedView>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: 70,
    borderRadius: 8,
    borderWidth: 1,
  },

  safeArea: {
    flex: 1,
    width: '100%',
    maxWidth: MaxContentWidth,
    alignSelf: 'center',
    alignItems: 'stretch',
    paddingHorizontal: Spacing.four,
    paddingBottom: BottomTabInset + Spacing.three,
    gap: Spacing.three,
    backgroundColor: '#333',
  },

  radioSection: {
    width: '100%',
    padding: 12,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#888',
    backgroundColor: '#444',
  },
});
