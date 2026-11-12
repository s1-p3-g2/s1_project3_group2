import { Image } from 'expo-image';
import { StyleSheet, Text, View } from 'react-native';

type DuckProfileProps = {
  name: string;
  imageLink: string;
  description: string;
  ratingScore: number;
  creatorId: number;
  position: number;
};

export function DuckProfile({
  name,
  imageLink,
  description,
  ratingScore,
  creatorId,
  position,
}: DuckProfileProps) {
  return (
    <View style={styles.card}>
      <Image
        source={{ uri: imageLink }}
        style={styles.image}
        contentFit="cover"
        accessibilityLabel={`${name}, rubber duck`}
      />
      <Text>"Duck Profile"</Text>
      <Text style={styles.name}>{name}</Text>
      <Text>{description}</Text>
      <Text>Rating: {ratingScore}</Text>
      <Text>Creator ID: {creatorId}</Text>
      <Text>Rank: {position}</Text>
    </View>
  );
}