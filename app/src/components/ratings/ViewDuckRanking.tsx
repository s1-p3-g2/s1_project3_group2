import {ActivityIndicator, Button, FlatList, StyleSheet, View} from "react-native";
import {
  getDuckRanking,
  getRatingByDuck,
  getRatingByUser,
  getRatings, RankingResponse,
  RatingResponse
} from "@/services/ratings-api";
import {useState} from "react";
import {ThemedView} from "@/components/themed-view";
import {ThemedText} from "@/components/themed-text";
import {BottomTabInset, MaxContentWidth, Spacing} from "@/constants/theme";
import {TextInput, useNativeState} from "@expo/ui";

export default function ViewDuckRankingModule() {
  const duckId = useNativeState('');
  const [ranking, setRanking] = useState<RankingResponse>();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Effect callback function
   * Sets useStates defined above and calls api service method
   */
  async function viewDuckRanking() {
    try {
      setLoading(true);
      setError(null);

      var duckIdNum = parseInt(duckId.get(), 10);
      if (isNaN(duckIdNum) || duckId.get().length == 0) {
        throw Error("Duck Id is not valid: Not a number or blank")
      }
      const result = await getDuckRanking(duckIdNum);
      setRanking(result);
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
          <ThemedText type="subtitle">Get Duck Ranking</ThemedText>
          <Button
              title={"Get Ranking"}
              onPress={viewDuckRanking}
              disabled={loading}
            />

          <ThemedText>
            Duck Id
          </ThemedText>
          <TextInput
              // style={styles.input}
              placeholder="eg. 1"
              value={duckId}
          />

          {loading && <ActivityIndicator />}

          {error && <ThemedText themeColor="textSecondary">Error: {error}</ThemedText>}

          {!loading && !error && ranking && (
              <>
                {displayRanking(ranking)}
              </>
          )}

        </ThemedView>
      </ThemedView>
  );
}

function displayRanking(item: RankingResponse) {
  return (
      <ThemedView style={styles.listRow}>
        <ThemedText>
          duck_id:{item.duckId}, score:{item.score}, position:{item.position}
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
