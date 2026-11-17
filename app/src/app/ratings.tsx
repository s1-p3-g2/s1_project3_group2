import { ScrollView, StyleSheet } from 'react-native';
import {ThemedText} from "@/components/themed-text";
import {ThemedView} from "@/components/themed-view";
import ViewRatingsModule from "@/components/ratings/viewRatings";
import CreateRatingsModule from "@/components/ratings/createRatings";
import {MaxContentWidth} from "@/constants/theme";
import UpdateRatingsModule from "@/components/ratings/updateRatings";

export default function RatingsScreen() {
  return (
      <ScrollView contentContainerStyle={styles.screen}>
        <ThemedText type="title" style={styles.title}>
          Duck Ratings
        </ThemedText>

        <ThemedText type="default">
          Create and view duck ratings.
        </ThemedText>

        <ThemedView style={styles.grid}>
          <ThemedView style={styles.card}>
            <ViewRatingsModule />
          </ThemedView>

          <ThemedView style={styles.card}>
            <CreateRatingsModule />
          </ThemedView>
          <ThemedView style={styles.card}>
            <UpdateRatingsModule />
          </ThemedView>
        </ThemedView>
      </ScrollView>
  );
}

const styles = StyleSheet.create({
  screen: {
    flexGrow: 1,
    width: '100%',
    alignItems: 'center',
    padding: 16,
    gap: 16,
  },

  title: {
    textAlign: 'center',
  },

  grid: {
    width: '100%',
    maxWidth: MaxContentWidth,
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    alignItems: 'flex-start',
    gap: 16,
  },

  card: {
    flexGrow: 1,
    flexBasis: 360,
    maxWidth: 520,
    minHeight: 250,
    padding: 16,
    borderRadius: 12,
    backgroundColor: '#333',
  },
});