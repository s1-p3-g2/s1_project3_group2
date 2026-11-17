import {ActivityIndicator, Button, FlatList, StyleSheet, View} from "react-native";
import {getRatings, RatingResponse} from "@/services/ratings-api";
import {useState} from "react";
import {ThemedView} from "@/components/themed-view";
import {ThemedText} from "@/components/themed-text";
import {BottomTabInset, MaxContentWidth, Spacing} from "@/constants/theme";

export default function ViewRatingsModule() {
  const [ratings, setRatings] = useState<RatingResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Effect callback function
   * Sets useStates defined above and calls api service method
   */
  async function viewRatings() {
    try {
      setLoading(true);
      setError(null);

      const results = await getRatings();
      setRatings(results);
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
          <ThemedText type="subtitle">Get Duck Ratings</ThemedText>
          <Button
              title={"Get Ratings"}
              onPress={viewRatings}
              disabled={loading}
            />
          {loading && <ActivityIndicator />}

          {error && <ThemedText themeColor="textSecondary">Error: {error}</ThemedText>}

          {!loading && !error && (
              <ThemedText>Total ratings: {ratings.length}</ThemedText>
          )}

          {!loading && !error && ratings.length > 0 && (
              <FlatList
                  data={ratings}
                  renderItem={ ({item}) =>
                      ratingRow(item)
                  }
                  keyExtractor={(rating) =>
                      `${rating.userId}-${rating.duckId}`
                  }
                  ListEmptyComponent={(
                        <ThemedText>No ratings loaded yet.</ThemedText>
                    )}
              />
          )}
        </ThemedView>
      </ThemedView>
  );
}

function ratingRow(item: RatingResponse) {
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
    paddingHorizontal: Spacing.four,
    alignItems: 'center',
    gap: Spacing.three,
    paddingBottom: BottomTabInset + Spacing.three,
    maxWidth: MaxContentWidth,
    backgroundColor: "dark grey"
  },
  title: {
    textAlign: 'center',
    paddingBottom: 30
  },
  listRow: {
    backgroundColor: 'transparent',
  }
});
