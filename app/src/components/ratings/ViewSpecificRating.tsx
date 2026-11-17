import {ActivityIndicator, Button, FlatList, StyleSheet, View} from "react-native";
import {getRating, RatingResponse} from "@/services/ratings-api";
import {useState} from "react";
import {ThemedView} from "@/components/themed-view";
import {ThemedText} from "@/components/themed-text";
import {BottomTabInset, MaxContentWidth, Spacing} from "@/constants/theme";
import {TextInput, useNativeState} from "@expo/ui";

export default function ViewRatingModule() {
  const userId = useNativeState('');
  const duckId = useNativeState('');
  const [rating, setRating] = useState<RatingResponse>();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Effect callback function
   * Sets useStates defined above and calls api service method
   */
  async function viewRating() {
    try {
      setLoading(true);
      setError(null);

      var duckIdNum = parseInt(duckId.get(), 10);
      if (isNaN(duckIdNum) || duckId.get().length == 0) {
        throw Error("Duck Id is not valid: Not a number or blank")
      }
      var userIdNum = parseInt(userId.get(), 10);
      if (isNaN(userIdNum) || userId.get().length == 0) {
        throw Error("User Id is not valid: Not a number or blank")
      }
      const result = await getRating(duckIdNum, userIdNum);
      setRating(result);
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
      <ThemedView style={styles.container}>

        <ThemedView style={styles.safeArea}>
          <ThemedText type="subtitle">Get Rating</ThemedText>
          <Button
              title={"Get Rating"}
              onPress={viewRating}
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

          {loading && <ActivityIndicator />}

          {error && <ThemedText themeColor="textSecondary">Error: {error}</ThemedText>}

          {!loading && !error && rating && (
              <>
                {ratingRow(rating)}
              </>
          )}
        </ThemedView>
      </ThemedView>
  );
}

function ratingRow(item: RatingResponse | undefined) {
  if (item == undefined) {
    return (<ThemedText themeColor="textSecondary">Error: Row is missing</ThemedText>)
  }
  return (
      <ThemedView style={styles.listRow}>
        <ThemedText>
          user_id:{item.userId}, duck_id:{item.duckId}, isPositive:{item.isPositive ? 'true' : 'false'}
        </ThemedText>
      </ThemedView>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingTop: 70
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
  title: {
    textAlign: 'center',
    paddingBottom: 30
  },
  listRow: {
    backgroundColor: 'transparent',
  }
});
